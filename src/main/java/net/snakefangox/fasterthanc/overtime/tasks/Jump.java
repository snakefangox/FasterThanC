package net.snakefangox.fasterthanc.overtime.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Jump implements OvertimeTask {
	private static final int MAX_TRY_OFFSET = 25;

	int index = 0;
	Stage stage = Stage.SETUP;
	List<BlockPos> shipPositions;
	List<BlockPos> destPositions = new ArrayList<>();
	BlockPos dest;
	World from;
	RegistryKey<World> toType;
	World to;
	List<Entity> entities = new ArrayList<>();
	boolean jumpObstructed = false;
	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;
	int minZ = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxY = Integer.MIN_VALUE;
	int maxZ = Integer.MIN_VALUE;
	int xSplit = 0;
	int zSplit = 0;
	int fieldX = 0;
	int fieldZ = 0;

	public Jump(List<BlockPos> shipPositions, BlockPos dest, World from, RegistryKey<World> toType) {
		this.shipPositions = shipPositions;
		this.dest = dest;
		this.from = from;
		this.toType = toType;
	}

	private static final int FLAGS = 2 | 16 | 32 | 64;

	@Override
	public void process(MinecraftServer server) {
		switch (stage) {
		case SETUP:
			if (index == 0) {
				to = server.getWorld(toType);
				if (shipPositions.isEmpty()) {
					stage = Stage.FINISHED;
					break;
				}
			}
			if (index < shipPositions.size()) {
				updateBounds(shipPositions.get(index));
				destPositions.add(shipPositions.get(index++).add(dest));
			} else {
				int xSize = maxX - minX;
				int zSize = maxZ - minZ;
				xSplit = (int) Math.ceil(xSize / 16.0);
				zSplit = (int) Math.ceil(zSize / 16.0);
				stage = Stage.CHECK;
				index = 0;
			}
			break;
		case CHECK:
			if (index < destPositions.size()) {
				BlockPos pos = destPositions.get(index++);
				jumpObstructed = World.isHeightInvalid(pos) || !to.isAir(pos) || jumpObstructed;
			} else {
				stage = Stage.TRANSFER;
				index = 0;
			}
			break;
		case TRANSFER:
			boolean op = false;
			if (index < destPositions.size()) {
				op = true;
				BlockState state = from.getBlockState(shipPositions.get(index));
				BlockEntity be = from.getBlockEntity(shipPositions.get(index));
				if (state.getBlock() instanceof BlockEntityProvider) {
					// Remove Old BE
					from.removeBlockEntity(shipPositions.get(index));
					// Non-Mojang Code May not Check For Null When Deleting the Block, So Replace It With An Empty BE
					from.setBlockEntity(shipPositions.get(index), ((BlockEntityProvider) state.getBlock()).createBlockEntity(to));
				}
				new BlockStateArgument(state, Collections.emptySet(), be != null ? be.toTag(new CompoundTag()) : null)
						.setBlockState((ServerWorld) to, destPositions.get(index), FLAGS);
				from.setBlockState(shipPositions.get(index), FRegister.jump_energy.getDefaultState(), FLAGS);
			}
			if (index < xSplit * zSplit) {
				op = true;
				int x = fieldX * 16;
				int z = fieldZ * 16;
				Box field = new Box(minX + x, minY, minZ + z, Math.min(minX + x + 16, maxX) + 1, maxY + 1, Math.min(minZ + z + 16, maxZ) + 1);
				List<Entity> temp = from.getEntities((Entity) null, field, EntityPredicates.VALID_ENTITY);
				temp.removeIf(e -> entities.contains(e));
				entities.addAll(temp);
				if (fieldX < xSplit) {
					++fieldX;
				} else {
					fieldX = 0;
					++fieldZ;
				}
			}
			++index;
			if (!op) {
				stage = Stage.FINALIZE;
				index = 0;
			}
			break;
		case FINALIZE:
			boolean opf = false;
			if (index < destPositions.size()) {
				opf = true;
				BlockPos posFrom = shipPositions.get(index);
				BlockPos posTo = destPositions.get(index);
				from.setBlockState(posFrom, Blocks.AIR.getDefaultState(), FLAGS);
				to.getBlockState(posTo).updateNeighbors(to, posTo, 2 | 32 | 64, 511);
				if (index % 8 == 0) {
					((ServerWorld) from).spawnParticles(ParticleTypes.LARGE_SMOKE, posFrom.getX() + 0.5, posFrom.getY() + 0.5, posFrom.getZ() + 0.5,
							3, 0.0, 0.0, 0.0, 0);
				}
			}
			if (index < entities.size()) {
				opf = true;
				Entity entity = entities.get(index);
				double x = entity.getX() + dest.getX();
				double y = entity.getY() + dest.getY();
				double z = entity.getZ() + dest.getZ();
				//Doesn't actually throw that exception so toss the catch
				try {
					TeleportCommand.teleport(null, entity, (ServerWorld) to, x, y, z, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class),
							entity.yaw, entity.pitch, null);
				} catch (CommandSyntaxException ignored) {
				}
			}
			++index;
			if (!opf) {
				index = 0;
				stage = Stage.FINISHED;
			}
			break;
		case FINISHED:
			break;
		}
	}

	private void updateBounds(BlockPos pos) {
		if (pos.getX() > maxX) {
			maxX = pos.getX();
		} else if (pos.getX() < minX) {
			minX = pos.getX();
		}
		if (pos.getY() > maxY) {
			maxY = pos.getY();
		} else if (pos.getY() < minY) {
			minY = pos.getY();
		}
		if (pos.getZ() > maxZ) {
			maxZ = pos.getZ();
		} else if (pos.getZ() < minZ) {
			minZ = pos.getZ();
		}
	}

	@Override
	public boolean isFinished() {
		if (jumpObstructed) {
			BlockPos newPos = dest.add((MAX_TRY_OFFSET / 2) - from.random.nextInt(MAX_TRY_OFFSET),
					(MAX_TRY_OFFSET / 2) - from.random.nextInt(MAX_TRY_OFFSET),
					(MAX_TRY_OFFSET / 2) - from.random.nextInt(MAX_TRY_OFFSET));
			newPos = new BlockPos(newPos.getX(), Math.min(200, Math.max(-200, newPos.getY())), newPos.getZ());
			OvertimeManager.addTask(new Jump(shipPositions, newPos, from, toType));
		}
		return jumpObstructed || stage == Stage.FINISHED;
	}

	@Override
	public boolean mustBeFinished() {
		return true;
	}

	enum Stage {
		SETUP, CHECK, TRANSFER, FINALIZE, FINISHED
	}
}

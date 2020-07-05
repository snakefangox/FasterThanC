package net.snakefangox.fasterthanc.overtime.tasks;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	boolean jumpObstructed = false;
	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;
	int minZ = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxY = Integer.MIN_VALUE;
	int maxZ = Integer.MIN_VALUE;

	public Jump(List<BlockPos> shipPositions, BlockPos dest, World from, RegistryKey<World> toType) {
		this.shipPositions = shipPositions;
		this.dest = dest;
		this.from = from;
		this.toType = toType;
	}

	@Override
	public void process(MinecraftServer server) {
		switch (stage) {
			case SETUP:
				if (to == null) {
					to = server.getWorld(toType);
				}
				if (index < shipPositions.size()) {
					updateBounds(shipPositions.get(index));
					destPositions.add(shipPositions.get(index++).add(dest));
				} else {
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
				if (index < destPositions.size()) {
					BlockState state = from.getBlockState(shipPositions.get(index));
					BlockEntity be = from.getBlockEntity(shipPositions.get(index));
					if (state.getBlock() instanceof BlockEntityProvider) {
						from.removeBlockEntity(shipPositions.get(index));
						// BlockStateArgument Will Not Save BE Data If a BE Does Not Exist
						to.setBlockEntity(destPositions.get(index), ((BlockEntityProvider) state.getBlock()).createBlockEntity(to));
					}
					new BlockStateArgument(state, Collections.emptySet(), be != null ? be.toTag(new CompoundTag()) : null)
							.setBlockState((ServerWorld) to, destPositions.get(index), 2 | 32 | 64);
					from.setBlockState(shipPositions.get(index), FRegister.jump_energy.getDefaultState(), 2 | 32 | 64);
					++index;
				} else {
					stage = Stage.FINALIZE;
					index = 0;
				}
				break;
			case FINALIZE:
				if (index == 0 && !shipPositions.isEmpty()) {
					Box field = new Box(minX, minY, minZ, maxX, maxY, maxZ).expand(1);
					List<? extends Entity> entities = from.getEntities((Entity) null, field, EntityPredicates.VALID_ENTITY);
					for (Entity entity : entities) {
						double x = entity.getX();
						double y = entity.getY();
						double z = entity.getZ();
						if (from != to)
							entity.changeDimension((ServerWorld) to);
						if (entity instanceof ServerPlayerEntity) {
							entity.teleport(x + dest.getX(), y + dest.getY(), z + dest.getZ());
						} else {
							entity.updatePosition(x + dest.getX(), y + dest.getY(), z + dest.getZ());
						}
					}
				}
				if (index < destPositions.size()) {
					BlockPos posFrom = shipPositions.get(index);
					++index;
					from.setBlockState(posFrom, Blocks.AIR.getDefaultState(), 2 | 32 | 64);
					((ServerWorld) from).spawnParticles(ParticleTypes.LARGE_SMOKE, posFrom.getX() + 0.5, posFrom.getY() + 0.5, posFrom.getZ() + 0.5,
							3, 0.0, 0.0, 0.0, 0);
				} else {
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
		SETUP, CHECK, TRANSFER, FINALIZE, FINISHED;
	}
}

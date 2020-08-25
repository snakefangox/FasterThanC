package net.snakefangox.fasterthanc.blocks;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.blocks.blockentities.EnergyManagementComputerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;
import net.snakefangox.fasterthanc.gui.EnergyComputerContainer;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;

public class EnergyManagementComputer extends HorizontalRotatableBlock implements BlockEntityProvider {

	public EnergyManagementComputer(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			sendEnergyNetToPlayer(pos, world, player);
		}
		return ActionResult.SUCCESS;
	}

	public void sendEnergyNetToPlayer(BlockPos pos, World world, PlayerEntity player) {
		Map<UUID, EnergyPackage> provide = null;
		Map<UUID, EnergyPackage> claim = null;
		Set<UUID> powered = null;
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		for (Direction dir : Direction.values()) {
			BlockPos blockPos = pos.offset(dir);
			BlockEntity be = world.getBlockEntity(blockPos);
			if (be instanceof EnergyHandler) {
				provide = ((EnergyHandler) be).getProviders();
				claim = ((EnergyHandler) be).getClaimants();
				powered = ((EnergyHandler) be).getPoweredDown();
			}
		}
		if (provide != null) {
			passedData.writeInt(provide.size() + claim.size());
			passedData.writeInt(claim.size());
			for (Map.Entry<UUID, EnergyPackage> entry : claim.entrySet()) {
				passedData.writeUuid(entry.getKey());
				passedData.writeText(entry.getValue().name);
				passedData.writeInt(entry.getValue().getAmount());
				passedData.writeBoolean(powered.contains(entry.getKey()));
			}
			for (Map.Entry<UUID, EnergyPackage> entry : provide.entrySet()) {
				passedData.writeUuid(entry.getKey());
				passedData.writeText(entry.getValue().name);
				passedData.writeInt(entry.getValue().getAmount());
				passedData.writeBoolean(powered.contains(entry.getKey()));
			}
		}
		if (provide != null) {
			ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Networking.ENERGY_NET_TO_CLIENT, passedData);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.energy_management_computer_type.instantiate();
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EnergyManagementComputerBE) {
			return new ExtendedScreenHandlerFactory() {
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new EnergyComputerContainer(syncId, inv, (EnergyManagementComputerBE) blockEntity);
				}

				@Override
				public Text getDisplayName() {
					return LiteralText.EMPTY;
				}

				@Override
				public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
					packetByteBuf.writeBlockPos(pos);
				}
			};
		} else {
			return null;
		}
	}
}

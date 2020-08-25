package net.snakefangox.fasterthanc.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.blocks.blockentities.EnergyManagementComputerBE;
import net.snakefangox.fasterthanc.blocks.blockentities.HardpointBE;
import net.snakefangox.fasterthanc.blocks.blockentities.TargetingComputerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;
import net.snakefangox.fasterthanc.gui.EnergyComputerContainer;
import net.snakefangox.fasterthanc.gui.TargetingComputerContainer;

import java.util.UUID;

public class TargetingComputer extends HorizontalRotatableBlock implements BlockEntityProvider {
	public TargetingComputer(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			sendHardPointsToPlayer(pos, world, player);
		}
		return ActionResult.SUCCESS;
	}

	public void sendHardPointsToPlayer(BlockPos pos, World world, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof TargetingComputerBE) {
			TargetingComputerBE be = (TargetingComputerBE) world.getBlockEntity(pos);
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			assert be != null;
			passedData.writeInt(be.getHardPoints().length);
			for (BlockPos hardpoint : be.getHardPoints()) {
				if (world.getBlockEntity(hardpoint) instanceof HardpointBE){
					HardpointBE hard = (HardpointBE) world.getBlockEntity(hardpoint);
					assert hard != null;
					passedData.writeUuid(hard.getEnergyID());
					passedData.writeText(hard.getName());
					passedData.writeFloat(hard.getPitch());
					passedData.writeFloat(hard.getYaw());
				} else {
					passedData.writeUuid(UUID.randomUUID());
					passedData.writeString("Invalid");
					passedData.writeFloat(0);
					passedData.writeFloat(0);
				}
			}
			ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Networking.HARDPOINTS_TO_CLIENT, passedData);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.targeting_computer_type.instantiate();
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof TargetingComputerBE) {
			return new ExtendedScreenHandlerFactory() {
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new TargetingComputerContainer(syncId, inv, (TargetingComputerBE) blockEntity);
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

package net.snakefangox.fasterthanc.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

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
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorControllerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;
import net.snakefangox.fasterthanc.gui.EnergyComputerContainer;
import net.snakefangox.fasterthanc.gui.ReactorControllerContainer;

public class ReactorController extends HorizontalRotatableBlock implements BlockEntityProvider {

	public ReactorController(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			sendReactorDataToPlayer(pos, world, player);
		}
		return ActionResult.SUCCESS;
	}

	public void sendReactorDataToPlayer(BlockPos pos, World world, PlayerEntity player) {
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReactorControllerBE) {
			ReactorControllerBE reactorControllerBE = (ReactorControllerBE)be;
			passedData.writeBoolean(reactorControllerBE.isComplete);
			passedData.writeInt(reactorControllerBE.chambers);
			passedData.writeInt(reactorControllerBE.remainingFuelTicks);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Networking.REACTOR_DATA, passedData);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.reactor_controller_type.instantiate();
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ReactorControllerBE) {
			return new ExtendedScreenHandlerFactory() {
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new ReactorControllerContainer(syncId, inv, (ReactorControllerBE) blockEntity);
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

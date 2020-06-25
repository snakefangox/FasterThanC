package net.snakefangox.fasterthanc.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorControllerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;

public class ReactorController extends HorizontalRotatableBlock implements BlockEntityProvider {

	public ReactorController(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			ContainerProviderRegistry.INSTANCE.openContainer(FRegister.reactor_container, player, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
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
}

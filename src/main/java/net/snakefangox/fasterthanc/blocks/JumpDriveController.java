package net.snakefangox.fasterthanc.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.blocks.blockentities.JumpDriveControllerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

public class JumpDriveController extends HorizontalRotatableBlock implements BlockEntityProvider {
	public JumpDriveController(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			if (world.getBlockEntity(pos) instanceof BlockEntityClientSerializable)
				((BlockEntityClientSerializable)world.getBlockEntity(pos)).sync();
			ContainerProviderRegistry.INSTANCE.openContainer(FRegister.jump_drive_container, player, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
			sendJumpDriveDataToPlayer(pos, world, player);
		}
		return ActionResult.SUCCESS;
	}

	public void sendJumpDriveDataToPlayer(BlockPos pos, World world, PlayerEntity player) {
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof JumpDriveControllerBE) {
			JumpDriveControllerBE reactorControllerBE = (JumpDriveControllerBE)be;
			passedData.writeBoolean(reactorControllerBE.isComplete);
			passedData.writeInt(reactorControllerBE.chambers);
			passedData.writeItemStack(reactorControllerBE.getStack(0));
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Networking.JUMP_DRIVE_DATA, passedData);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof JumpDriveControllerBE && world.getReceivedRedstonePower(pos) > 0) {
			((JumpDriveControllerBE)be).startJump();
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		if (world.getBlockEntity(pos) instanceof SimpleInventory && !world.isClient) {
			SimpleInventory be = (SimpleInventory) world.getBlockEntity(pos);
			for (ItemStack stack : be.getItems()) {
				ItemEntity ie = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(ie);
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.jump_drive_controller_type.instantiate();
	}
}

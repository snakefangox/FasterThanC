package net.snakefangox.fasterthanc.blocks;

import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.DeorbitorBE;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Deorbitor extends Block implements BlockEntityProvider {
	public Deorbitor(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof DeorbitorBE) {
			ItemStack heldStack = player.getStackInHand(hand);
			DeorbitorBE deorbitorBE = (DeorbitorBE) world.getBlockEntity(pos);
			if (heldStack.getItem() == FRegister.hyper_magnet && deorbitorBE.getStack(0).isEmpty()) {
				ItemStack stack = new ItemStack(heldStack.getItem(), 1);
				if (!player.isCreative()) {
					heldStack.decrement(1);
				}
				deorbitorBE.setStack(0, stack);
				deorbitorBE.sync();
			} else if (heldStack.isEmpty() && !deorbitorBE.getStack(0).isEmpty()) {
				player.setStackInHand(hand, deorbitorBE.getStack(0));
				deorbitorBE.setStack(0, ItemStack.EMPTY);
				deorbitorBE.sync();
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		boolean powered = world.getReceivedRedstonePower(pos) > 0;
		if (powered) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof DeorbitorBE) {
				((DeorbitorBE) be).startCall();
			}
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		if (world.getBlockEntity(pos) instanceof SimpleInventory && !world.isClient()) {
			SimpleInventory be = (SimpleInventory) world.getBlockEntity(pos);
			for (ItemStack stack : be.getItems()) {
				ItemEntity ie = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(ie);
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.deorbiter_type.instantiate();
	}
}

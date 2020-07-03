package net.snakefangox.fasterthanc.blocks;

import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.HardpointBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;
import net.snakefangox.fasterthanc.items.ShipWeapon;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Hardpoint extends HorizontalRotatableBlock implements BlockEntityProvider {

	public static final BooleanProperty DEPLOYED = BooleanProperty.of("deployed");

	public Hardpoint(Settings settings) {
		super(settings, true);
		setDefaultState(getDefaultState().with(DEPLOYED, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof HardpointBE) {
			ItemStack heldStack = player.getStackInHand(hand);
			HardpointBE hardpointBE = (HardpointBE) world.getBlockEntity(pos);
			if (heldStack.getItem() instanceof ShipWeapon && hardpointBE.getStack(0).isEmpty()) {
				ItemStack stack = new ItemStack(heldStack.getItem(), 1);
				if (!player.isCreative()) {
					heldStack.decrement(1);
				}
				hardpointBE.setStack(0, stack);
				hardpointBE.handleChanges();
			} else if (heldStack.isEmpty() && !hardpointBE.getStack(0).isEmpty()) {
				player.setStackInHand(hand, hardpointBE.getStack(0));
				hardpointBE.setStack(0, ItemStack.EMPTY);
				hardpointBE.handleChanges();
			}
		}
		return ActionResult.SUCCESS;
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
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		boolean deployed = state.get(DEPLOYED);
		boolean powered = world.getReceivedRedstonePower(pos) > 0;
		if (!deployed && powered) {
			world.setBlockState(pos, state.with(DEPLOYED, true));
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof HardpointBE) {
				be.resetBlock();
			}
		} else if (deployed && !powered) {
			world.setBlockState(pos, state.with(DEPLOYED, false));
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof HardpointBE) {
				be.resetBlock();
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(DEPLOYED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.hardpoint_type.instantiate();
	}
}

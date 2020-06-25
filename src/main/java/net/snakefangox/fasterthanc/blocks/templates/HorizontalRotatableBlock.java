package net.snakefangox.fasterthanc.blocks.templates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public class HorizontalRotatableBlock extends Block {

	final boolean placeOpposite;

	public HorizontalRotatableBlock(Settings settings, boolean placeOpposite) {
		super(settings);
		this.placeOpposite = placeOpposite;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction dir = Direction.fromHorizontal(ctx.getPlayerLookDirection().getHorizontal());
		if (placeOpposite) dir = dir.getOpposite();
		return getDefaultState().with(HorizontalFacingBlock.FACING, dir);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING);
	}
}

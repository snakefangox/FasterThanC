package net.snakefangox.fasterthanc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class ReactorTank extends Block {

	public static final BooleanProperty FILLED = BooleanProperty.of("filled");

	public ReactorTank(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FILLED, false));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FILLED, false);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FILLED);
	}
}

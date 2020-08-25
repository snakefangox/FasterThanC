package net.snakefangox.fasterthanc.blocks;


import net.snakefangox.fasterthanc.items.BeaconCoordsStorage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JumpBeacon extends Block {
	private static final VoxelShape BOX = VoxelShapes.cuboid(0.28, 0.125, 0.28, 0.72, 1.2, 0.72);

	public JumpBeacon(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BOX;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			if (player.getStackInHand(hand).getItem() instanceof BeaconCoordsStorage) {
				BeaconCoordsStorage.addCoords(player.getStackInHand(hand), pos, world);
			}
		}
		return ActionResult.SUCCESS;
	}
}

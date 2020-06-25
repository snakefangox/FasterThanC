package net.snakefangox.fasterthanc.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.items.BeaconCoordsStorage;

import java.util.List;

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
			List<Entity> entities = world.getEntities(null, new Box(pos.add(-2, -2, -2), pos.add(2, 2, 2)));
			for (Entity e : entities) {
				if (e instanceof ItemEntity)
					return ActionResult.SUCCESS;
			}
			ItemStack stack = new ItemStack(FRegister.beacon_coord_chip);
			BeaconCoordsStorage.addCoords(stack, pos, world);
			ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
			world.spawnEntity(itemEntity);
		}
		return ActionResult.SUCCESS;
	}
}

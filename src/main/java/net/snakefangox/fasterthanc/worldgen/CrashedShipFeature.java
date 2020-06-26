package net.snakefangox.fasterthanc.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorPortBE;

import java.util.Random;

public class CrashedShipFeature extends Feature<DefaultFeatureConfig> {

	private final Block[] SHIP_PARTS = new Block[]{FRegister.reactor_casing, FRegister.reactor_casing, FRegister.reactor_casing,
			FRegister.reactor_casing,FRegister.reactor_casing, FRegister.reactor_casing, FRegister.reactor_casing, FRegister.reactor_casing,
			FRegister.jump_drive_casing, FRegister.jump_drive_casing, FRegister.jump_drive_casing, FRegister.jump_drive_casing, FRegister.reactor_chamber,
			FRegister.reactor_controller, FRegister.reactor_energy_port,
			FRegister.reactor_tank, FRegister.reactor_glass, FRegister.reactor_input, FRegister.reactor_output,
			FRegister.jump_drive_casing, FRegister.reactor_casing, FRegister.jump_drive_controller, FRegister.jump_drive_energy_port,
			FRegister.jump_drive_field_chamber, FRegister.targeting_computer, FRegister.energy_management_computer, FRegister.hardpoint};

	public CrashedShipFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(ServerWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator generator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		int debris = random.nextInt(224) + 1;
		BlockState hullState = FRegister.HULL_BLOCKS.get(random.nextInt(FRegister.HULL_BLOCKS.size())).getDefaultState();
		for (int i = 0; i < debris; ++i) {
			BlockPos topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE,
					pos.add(random.nextInt(random.nextInt(5) + 24),
							random.nextInt(3) - 2,
							random.nextInt(random.nextInt(5) + 24)));
			if (random.nextBoolean()) {
				world.setBlockState(topPos, SHIP_PARTS[random.nextInt(SHIP_PARTS.length)].getDefaultState(), 3);
				if (world.getBlockEntity(topPos) instanceof ReactorPortBE) {
					((ReactorPortBE)world.getBlockEntity(topPos)).setStack(random.nextInt(4), new ItemStack(FRegister.fuel_cell, random.nextInt(3) + 1));
				}
			} else {
				world.setBlockState(topPos, hullState, 3);
			}
		}

		int lava = random.nextInt(11) + 2;
		for (int i = 0; i < lava; ++i) {
			BlockPos topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos.add(random.nextInt(16), 0, random.nextInt(16)));
			world.setBlockState(topPos, Blocks.LAVA.getDefaultState(), 3);
			world.getFluidTickScheduler().schedule(topPos, Fluids.LAVA, 12);
		}

		return true;
	}
}

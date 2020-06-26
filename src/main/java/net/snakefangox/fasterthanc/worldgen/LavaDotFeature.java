package net.snakefangox.fasterthanc.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.snakefangox.fasterthanc.FRegister;

import java.util.Random;

public class LavaDotFeature extends Feature<DefaultFeatureConfig> {

	public LavaDotFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(ServerWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator generator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		int lava = random.nextInt(11);
		for (int i = 0; i < lava; ++i) {
			BlockPos topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos.add(random.nextInt(16), 0, random.nextInt(16)));
			world.setBlockState(topPos, Blocks.LAVA.getDefaultState(), 3);
			world.getFluidTickScheduler().schedule(topPos, Fluids.LAVA, 12);
		}
		return true;
	}
}

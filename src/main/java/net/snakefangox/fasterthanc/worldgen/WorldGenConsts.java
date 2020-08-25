package net.snakefangox.fasterthanc.worldgen;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class WorldGenConsts {

	public static NoiseConfig FLOATING_ISLAND_NOISE_CONFIG = new NoiseConfig(128, new NoiseSamplingConfig(2.0D, 1.0D, 80.0D, 160.0D), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0D, 0.0D, true, false, false, false);

	public static ChunkGeneratorType SECTOR_TYPE = new ChunkGeneratorType(new StructuresConfig(Optional.empty(), new HashMap<>()), FLOATING_ISLAND_NOISE_CONFIG, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), -1, 300, -1, false);
}

package net.snakefangox.fasterthanc.worldgen;

import net.minecraft.block.Blocks;
import net.minecraft.client.sound.MusicType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.MusicSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.snakefangox.fasterthanc.FRegister;

public class ShipGraveyardBiome extends Biome {

	private static final TernarySurfaceConfig SCORCHED_CONFIG =
			new TernarySurfaceConfig(Blocks.BASALT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.LAVA.getDefaultState());

	public ShipGraveyardBiome() {
		super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SCORCHED_CONFIG)
				.precipitation(Precipitation.NONE).category(Category.DESERT).depth(0.5F).scale(0.1F).temperature(1F)
				.downfall(0.3F).effects(new BiomeEffects.Builder().fogColor(0x99CC00).waterColor(0x66CC99).waterFogColor(0x66CC99)
				.music(new MusicSound(FRegister.DARK_HALLS, 100, 600, true))
				.particleConfig(new BiomeParticleConfig(ParticleTypes.ASH, 0.5F)).build()).parent((String)null));
		DefaultBiomeFeatures.addLandCarvers(this);
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.NETHER_CAVE, new ProbabilityConfig(0.9F)));
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.CANYON, new ProbabilityConfig(0.4F)));
		DefaultBiomeFeatures.addDesertLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		addFeature(GenerationStep.Feature.RAW_GENERATION, FRegister.CRASHED_SHIP.configure(new DefaultFeatureConfig())
				.createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(20))));
		addFeature(GenerationStep.Feature.RAW_GENERATION, FRegister.LAVA_DOT.configure(new DefaultFeatureConfig())
				.createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(5))));
	}
}

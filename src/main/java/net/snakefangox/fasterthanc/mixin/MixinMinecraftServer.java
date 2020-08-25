package net.snakefangox.fasterthanc.mixin;

import java.util.Map;
import java.util.concurrent.Executor;

import com.google.common.collect.ImmutableList;
import net.snakefangox.fasterthanc.tools.DynamicDimGen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable, DynamicDimGen {

	@Final
	@Shadow
	protected SaveProperties saveProperties;
	@Final
	@Shadow
	private Executor workerExecutor;
	@Final
	@Shadow
	private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
	@Final
	@Shadow
	protected LevelStorage.Session session;
	@Final
	@Shadow
	private Map<RegistryKey<World>, ServerWorld> worlds;

	public MixinMinecraftServer(String string) {
		super(string);
	}

	@Override
	public void createDynamicDim(RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, DimensionOptions dimensionOptions) {
		ServerWorldProperties serverWorldProperties = saveProperties.getMainWorldProperties();
		GeneratorOptions generatorOptions = saveProperties.getGeneratorOptions();
		WorldBorder worldBorder = worlds.get(DimensionOptions.OVERWORLD).getWorldBorder();
		RegistryKey<World> worldRegistryKey = RegistryKey.of(Registry.DIMENSION, dimensionOptionsRegistryKey.getValue());
		WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
		boolean debugWorld = generatorOptions.isDebugWorld();
		long hashSeed = BiomeAccess.hashSeed(generatorOptions.getSeed());
		DimensionType dimensionType3 = dimensionOptions.getDimensionType();
		ChunkGenerator chunkGenerator3 = dimensionOptions.getChunkGenerator();
		UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(saveProperties, serverWorldProperties);
		ServerWorld serverWorld2 = new ServerWorld((MinecraftServer) (Object) this, workerExecutor, session, unmodifiableLevelProperties, worldRegistryKey, dimensionType3, worldGenerationProgressListener, chunkGenerator3, debugWorld, hashSeed, ImmutableList.of(), false);
		worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(serverWorld2.getWorldBorder()));
		this.worlds.put(worldRegistryKey, serverWorld2);
	}
}

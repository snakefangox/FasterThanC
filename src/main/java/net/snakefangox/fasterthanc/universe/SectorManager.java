package net.snakefangox.fasterthanc.universe;

import java.util.Collection;

import net.snakefangox.fasterthanc.tools.DynamicDimGen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

public class SectorManager {

	private static UniverseData data;

	public static void serverStart(MinecraftServer server) {
		data = UniverseData.getInstance(server);
		for (Identifier sec : data.sectors) {
			createSector(server, sec, false);
		}
	}

	public static void serverStop(MinecraftServer server) {
		data.markDirty();
		data = null;
	}

	public static void createSpaceDim(MinecraftServer server, Identifier identifier, DimensionOptions dimensionOptions, Collection<Identifier> toAdd) {
		((DynamicDimGen) server).createDynamicDim(RegistryKey.of(Registry.DIMENSION_OPTIONS, identifier), dimensionOptions);
		if (toAdd != null) {
			toAdd.add(identifier);
		}
	}

	public static void createSector(MinecraftServer server, Identifier identifier, boolean add) {
		
	}
}

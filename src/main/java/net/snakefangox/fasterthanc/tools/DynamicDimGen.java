package net.snakefangox.fasterthanc.tools;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

public interface DynamicDimGen {
	void createDynamicDim(RegistryKey<DimensionOptions> dimensionOptionsRegistryKey, DimensionOptions dimensionOptions);
}

package net.snakefangox.fasterthanc.energy;

import net.minecraft.util.math.Direction;

public interface Energy {
	int ENERGY_TICK = 20;

	default boolean canCableConnect(Direction dir) {
		return true;
	}
}

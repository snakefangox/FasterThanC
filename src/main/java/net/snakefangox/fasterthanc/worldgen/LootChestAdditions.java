package net.snakefangox.fasterthanc.worldgen;

import net.snakefangox.fasterthanc.FRegister;

import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;

public class LootChestAdditions {

	private static final Identifier END_CITY_LOOT_TABLE_ID = new Identifier("minecraft", "chests/end_city_treasure");


	public static void registerEndCityLoot(ResourceManager rm, LootManager lm, Identifier id, FabricLootSupplierBuilder fsb, LootTableLoadingCallback.LootTableSetter lts) {
		if (END_CITY_LOOT_TABLE_ID.equals(id)) {
			FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1))
							.withEntry(ItemEntry.builder(FRegister.hyper_magnet).build())
							.withCondition(RandomChanceLootCondition.builder(0.65F).build());
			fsb.withPool(poolBuilder.build());
		}
	}
}

package net.snakefangox.fasterthanc;

import java.io.File;
import java.io.IOException;

import net.snakefangox.fasterthanc.energy.CableNetworkStorage;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.tools.AutoGenJson;
import net.snakefangox.fasterthanc.worldgen.LootChestAdditions;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.loader.api.FabricLoader;

public class FasterThanC implements ModInitializer {
	public static final String MODID = "fasterthanc";

	public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(FRegister.jump_drive_controller));
	public static final ItemGroup HULL = FabricItemGroupBuilder.build(new Identifier(MODID, "hull"),
			() -> new ItemStack(FRegister.HULL_BLOCKS.get(0)));

	@Override
	public void onInitialize() {
		FRegister.registerEverything();
		Networking.registerToServer();

		ServerTickEvents.END_SERVER_TICK.register(OvertimeManager::serverTick);
		ServerTickEvents.END_SERVER_TICK.register(OvertimeManager::ServerClosing);
		//ServerLifecycleEvents.SERVER_STARTED.register(SectorManager::serverStart);
		//ServerLifecycleEvents.SERVER_STOPPING.register(SectorManager::serverStop);
		ServerTickEvents.END_WORLD_TICK.register(CableNetworkStorage::tickPipes);
		LootTableLoadingCallback.EVENT.register(LootChestAdditions::registerEndCityLoot);

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			try {
				AutoGenJson.autoGenerateJson(MODID, new File("..").getCanonicalPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

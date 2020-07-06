package net.snakefangox.fasterthanc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.snakefangox.fasterthanc.energy.CableNetworkStorage;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.tools.AutoGenJson;

import java.io.File;
import java.io.IOException;

public class FasterThanC implements ModInitializer {
	public static final String MODID = "fasterthanc";

	public static final ItemGroup GENERAL = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(FRegister.jump_drive_controller));
	public static final ItemGroup HULL = FabricItemGroupBuilder.build(new Identifier(MODID, "hull"),
			() -> new ItemStack(FRegister.HULL_BLOCKS.get(0)));

	@Override
	public void onInitialize() {
		FRegister.registerEverything();
		Networking.registerToServer();

		ServerTickCallback.EVENT.register(OvertimeManager::serverTick);
		ServerStopCallback.EVENT.register(OvertimeManager::ServerClosing);
		WorldTickCallback.EVENT.register(CableNetworkStorage::tickPipes);

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			try {
				AutoGenJson.autoGenerateJson(MODID, new File("..").getCanonicalPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

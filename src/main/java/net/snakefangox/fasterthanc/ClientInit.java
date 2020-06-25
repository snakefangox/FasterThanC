package net.snakefangox.fasterthanc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.snakefangox.fasterthanc.blocks.blockentities.rendering.HardpointBER;
import net.snakefangox.fasterthanc.gui.*;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		ScreenProviderRegistry.INSTANCE.registerFactory(FRegister.five_slot_container, FiveSlotContainerScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(FRegister.reactor_container, ReactorControllerScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(FRegister.jump_drive_container, JumpDriveControllerScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(FRegister.energy_computer_container, EnergyComputerScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(FRegister.targeting_computer_container, TargetingComputerScreen::new);

		BlockEntityRendererRegistry.INSTANCE.register(FRegister.hardpoint_type, HardpointBER::new);

		FRegister.setRenderLayers();
		Networking.registerToClient();
	}
}

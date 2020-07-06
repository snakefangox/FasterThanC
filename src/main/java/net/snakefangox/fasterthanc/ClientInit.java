package net.snakefangox.fasterthanc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.snakefangox.fasterthanc.blocks.HolographicSky;
import net.snakefangox.fasterthanc.blocks.blockentities.rendering.HardpointBER;
import net.snakefangox.fasterthanc.blocks.blockentities.rendering.HolographicSkyBER;
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
		BlockEntityRendererRegistry.INSTANCE.register(FRegister.holographic_sky_type, HolographicSkyBER::new);

		BuiltinItemRendererRegistry.INSTANCE.register(FRegister.holographic_sky.asItem(), new BuiltinItemRenderer() {
			private final BlockEntity entity = new HolographicSky.BE();
			@Override
			public void render(ItemStack itemStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
				matrixStack.push();
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(entity, matrixStack, vertexConsumerProvider, light, overlay);
				matrixStack.pop();
			}
		});

		FRegister.setRenderLayers();
		Networking.registerToClient();
	}
}

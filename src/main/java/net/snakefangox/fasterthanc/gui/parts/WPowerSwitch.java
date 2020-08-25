package net.snakefangox.fasterthanc.gui.parts;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.snakefangox.fasterthanc.FasterThanC;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.gui.EnergyComputerContainer;
import spinnery.client.render.BaseRenderer;
import spinnery.widget.WToggle;

import java.util.UUID;

public class WPowerSwitch extends WToggle {
	private static final Identifier ON = new Identifier(FasterThanC.MODID, "textures/gui/button_on.png");
	private static final Identifier OFF = new Identifier(FasterThanC.MODID, "textures/gui/button_off.png");

	private UUID uuid;
	private EnergyComputerContainer container;

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		super.onMouseClicked(mouseX, mouseY, mouseButton);
		PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeBlockPos(container.controllerBE.getPos());
		passedData.writeUuid(uuid);
		passedData.writeBoolean(toggleState);
		ClientSidePacketRegistry.INSTANCE.sendToServer(Networking.ENERGY_NET_TO_SERVER, passedData);
	}

	public void setContainer(EnergyComputerContainer container) {
		this.container = container;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
		BaseRenderer.drawTexturedQuad(matrices, provider, getX(), getY(), getZ(), getWidth(), getHeight(),
				toggleState ? OFF : ON);
	}
}

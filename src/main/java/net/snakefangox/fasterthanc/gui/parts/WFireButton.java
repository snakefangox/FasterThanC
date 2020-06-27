package net.snakefangox.fasterthanc.gui.parts;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.FasterThanC;
import net.snakefangox.fasterthanc.Networking;
import spinnery.client.render.BaseRenderer;
import spinnery.widget.WAbstractButton;

@Environment(EnvType.CLIENT)
public class WFireButton extends WAbstractButton {

	int id = 0;
	BlockPos pos = new BlockPos(0, 0, 0);

	public static final Identifier FIRE = new Identifier(FasterThanC.MODID, "textures/gui/button_fire.png");

	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
		super.draw(matrices, provider);
		BaseRenderer.drawTexturedQuad(matrices, provider, getX(), getY(), getZ(), getWidth(), getHeight(), FIRE);
	}

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		super.onMouseClicked(mouseX, mouseY, mouseButton);
		if (isWithinBounds(mouseX, mouseY)) {
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeBlockPos(pos);
			passedData.writeInt(id);
			ClientSidePacketRegistry.INSTANCE.sendToServer(Networking.FIRE_TO_SERVER, passedData);
		}
	}

	public int getId() {
		return id;
	}

	public WFireButton setId(int id) {
		this.id = id;
		return this;
	}

	public BlockPos getPos() {
		return pos;
	}

	public WFireButton setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}
}

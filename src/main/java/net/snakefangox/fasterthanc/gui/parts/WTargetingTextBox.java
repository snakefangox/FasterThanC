package net.snakefangox.fasterthanc.gui.parts;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.Networking;
import spinnery.widget.WTextField;

public class WTargetingTextBox extends WTextField {

	int id = 0;
	BlockPos pos = new BlockPos(0,0,0);
	WTargetingTextBox twin;
	boolean isPitch = false;

	@Override
	public void onCharTyped(char character, int keyCode) {
		super.onCharTyped(character, keyCode);
		try {
			float value = Float.valueOf(getText());
			float twinValue = Float.valueOf(twin.getText());
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeBlockPos(pos);
			passedData.writeInt(id);
			passedData.writeFloat(isPitch ? value : twinValue);
			passedData.writeFloat(isPitch ? twinValue : value);
			ClientSidePacketRegistry.INSTANCE.sendToServer(Networking.ANZIMITH_TO_SERVER, passedData);
		}catch (Exception e) {}
	}

	public int getId() {
		return id;
	}

	public WTargetingTextBox setId(int id) {
		this.id = id;
		return this;
	}

	public BlockPos getPos() {
		return pos;
	}

	public WTargetingTextBox setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	public WTargetingTextBox setTwin(WTargetingTextBox twin) {
		this.twin = twin;
		return this;
	}

	public WTargetingTextBox setPitch(boolean pitch) {
		isPitch = pitch;
		return this;
	}
}

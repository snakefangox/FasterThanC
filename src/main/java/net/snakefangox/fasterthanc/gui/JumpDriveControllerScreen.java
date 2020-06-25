package net.snakefangox.fasterthanc.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.items.BeaconCoordsStorage;
import spinnery.client.screen.BaseContainerScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class JumpDriveControllerScreen extends BaseContainerScreen<JumpDriveControllerContainer> {

	JumpDriveControllerContainer linkedContainer;
	WStaticText formed;
	WStaticText chambers;
	WStaticText destDim;
	WStaticText destPos;

	public JumpDriveControllerScreen(JumpDriveControllerContainer linkedContainer) {
		super(new LiteralText(""), linkedContainer, linkedContainer.player);
		this.linkedContainer = linkedContainer;
		WInterface wInterface = getInterface();
		wInterface.setTheme("spinnery:dark");
		WPanel mainPanel = wInterface.createChild(WPanel::new, Position.of(0, 0, 0),
				Size.of(9 * 18 + 8, 3 * 18 + 118)).setParent(wInterface);
		mainPanel.setOnAlign(WAbstractWidget::center);
		mainPanel.center();
		wInterface.add(mainPanel);
		WSlot.addPlayerInventory(Position.of(mainPanel, ((mainPanel.getWidth()) / 2) - (int) (18 * 4.5f), 3 * 18 + 34, 1),
				Size.of(18, 18), wInterface);
		formed = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 5, 1), Size.of(120, 18));
		chambers = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 25, 1), Size.of(120, 18));
		WSlot.addArray(Position.of(mainPanel, 140, 35, 1), Size.of(18, 18), mainPanel, 0,
				JumpDriveControllerContainer.INV, 1, 1);
		destDim = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 45, 1), Size.of(120, 18));
		mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 60, 1), Size.of(120, 18)).setText("Target position: ");
		destPos = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 70, 1), Size.of(120, 18));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
		if (linkedContainer.shouldUpdate) {
			linkedContainer.shouldUpdate = false;
			boolean valid = linkedContainer.complete;
			formed.setText((valid ? Formatting.GREEN : Formatting.RED) + "Jump Drive Complete: " + (valid ? "True" : "False"));
			chambers.setText("Jump Factor: " + linkedContainer.chambers);
			if (linkedContainer.stack.getItem() == FRegister.beacon_coord_chip) {
				destDim.setText("Destination: " + BeaconCoordsStorage.getDim(linkedContainer.stack).getValue().getPath());
				destPos.setText(BeaconCoordsStorage.getPos(linkedContainer.stack).toShortString());
			} else {
				destDim.setText("Destination: Blind Jump");
				destPos.setText("Blind Jump");
			}
		}
	}
}

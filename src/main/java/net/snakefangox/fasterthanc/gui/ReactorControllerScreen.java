package net.snakefangox.fasterthanc.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import spinnery.client.screen.BaseContainerScreen;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class ReactorControllerScreen extends BaseHandledScreen<ReactorControllerContainer> {

	ReactorControllerContainer linkedContainer;
	WStaticText formed;
	WStaticText chambers;
	WStaticText remainingTime;
	WStaticText powerOutput;

	public ReactorControllerScreen(ReactorControllerContainer handler, PlayerInventory inventory, Text title) {
		super(title, handler, inventory.player);
		this.linkedContainer = handler;
		WInterface wInterface = getInterface();
		wInterface.setTheme("spinnery:dark");
		WPanel mainPanel = wInterface.createChild(WPanel::new, Position.of(0, 0, 0),
				Size.of(9 * 18 + 8, 3 * 18 + 108)).setParent(wInterface);
		mainPanel.setOnAlign(WAbstractWidget::center);
		mainPanel.center();
		wInterface.add(mainPanel);
		WSlot.addPlayerInventory(Position.of(mainPanel, ((mainPanel.getWidth()) / 2) - (int) (18 * 4.5f), 3 * 18 + 24, 1),
				Size.of(18, 18), wInterface);
		formed = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 5, 1), Size.of(120, 18));
		boolean valid = linkedContainer.complete;
		formed.setText((valid ? Formatting.GREEN : Formatting.RED) + "Reactor Complete: " + (valid ? "True" : "False"));
		chambers = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 25, 1), Size.of(120, 18));
		remainingTime = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 35, 1), Size.of(120, 18));
		powerOutput = mainPanel.createChild(WStaticText::new, Position.of(mainPanel, 5, 45, 1), Size.of(120, 18));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, tickDelta);
		if (linkedContainer.shouldUpdate) {
			linkedContainer.shouldUpdate = false;
			boolean valid = linkedContainer.complete;
			formed.setText((valid ? Formatting.GREEN : Formatting.RED) + "Reactor Complete: " + (valid ? "True" : "False"));
			chambers.setText("Chamber Count: " + linkedContainer.chambers);
			remainingTime.setText("Remaining Fuel Time: " + linkedContainer.remainingFuel / 20 + " secs");
			powerOutput.setText("Power Output: " + (valid && linkedContainer.remainingFuel > 0
					? linkedContainer.chambers : 0));
		}
	}
}

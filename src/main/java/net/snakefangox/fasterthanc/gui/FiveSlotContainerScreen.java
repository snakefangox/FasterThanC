package net.snakefangox.fasterthanc.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import spinnery.client.screen.BaseContainerScreen;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class FiveSlotContainerScreen extends BaseHandledScreen<FiveSlotContainer> {

	public FiveSlotContainerScreen(FiveSlotContainer handler, PlayerInventory inventory, Text title) {
		super(title, handler, inventory.player);

		WInterface wInterface = getInterface();
		wInterface.setTheme("spinnery:dark");
		WPanel mainPanel = wInterface.createChild(WPanel::new, Position.of(0,0,0), Size.of(9 * 18 + 8, 3 * 18 + 108)).setParent(wInterface);
		mainPanel.setOnAlign(WAbstractWidget::center);
		mainPanel.center();
		wInterface.add(mainPanel);
		WSlot.addPlayerInventory(Position.of(mainPanel, ((mainPanel.getWidth()) / 2) - (int) (18 * 4.5f), 3 * 18 + 24, 1),
				Size.of(18, 18), wInterface);
		WSlot.addArray(Position.of(mainPanel, (mainPanel.getWidth()/2) - (5 * 18) / 2, 20, 1), Size.of(18, 18),
				wInterface, 0, FiveSlotContainer.INV, 5, 1);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, tickDelta);
	}
}
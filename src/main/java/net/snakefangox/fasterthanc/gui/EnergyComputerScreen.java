package net.snakefangox.fasterthanc.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.snakefangox.fasterthanc.gui.parts.WBlankPanel;
import net.snakefangox.fasterthanc.gui.parts.WPowerSwitch;
import net.snakefangox.fasterthanc.tools.TranslateHelper;
import spinnery.client.screen.BaseContainerScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class EnergyComputerScreen extends BaseContainerScreen<EnergyComputerContainer> {

	WVerticalScrollableContainer energyNetScroll;
	EnergyComputerContainer linkedContainer;

	public EnergyComputerScreen(EnergyComputerContainer linkedContainer) {
		super(new LiteralText(""), linkedContainer, linkedContainer.player);
		this.linkedContainer = linkedContainer;
		WInterface wInterface = getInterface();
		wInterface.setTheme("spinnery:dark");
		WPanel mainPanel = wInterface.createChild(WPanel::new, Position.of(0, 0, 0),
				Size.of(9 * 18 + 8, 3 * 18 + 148)).setParent(wInterface);
		mainPanel.setOnAlign(WAbstractWidget::center);
		mainPanel.center();
		wInterface.add(mainPanel);
		WSlot.addPlayerInventory(Position.of(mainPanel, ((mainPanel.getWidth()) / 2) - (int) (18 * 4.5f), 3 * 18 + 64, 1),
				Size.of(18, 18), wInterface);
		energyNetScroll = mainPanel.createChild(WVerticalScrollableContainer::new, Position.of(mainPanel, 5, 5, 1),
				Size.of(mainPanel.getWidth() - 10, (mainPanel.getHeight() / 2) - 2));
		energyNetScroll.onAlign();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
		if (linkedContainer.shouldUpdate && linkedContainer.uuids.length > 0) {
			linkedContainer.shouldUpdate = false;
			float offset = energyNetScroll.getOffsetY();
			energyNetScroll.remove(energyNetScroll.getWidgets().toArray(new WAbstractWidget[energyNetScroll.getWidgets().size()]));
			for (int j = 0; j < linkedContainer.uuids.length; j++) {
				WBlankPanel panel = energyNetScroll.createChild(WBlankPanel::new, Position.of(energyNetScroll, 0, 16 * j),
						Size.of(energyNetScroll.getWidth() - energyNetScroll.getScrollbarWidth() - 8, 30));
				panel.setIndex(j);
				panel.createChild(WStaticText::new, Position.of(panel, 20, 2, 10), Size.of(100, 16))
						.setText(TranslateHelper.translateIfNeeded(linkedContainer.names[j]));
				WPowerSwitch powerSwitch = panel.createChild(WPowerSwitch::new, Position.of(panel, 0, 2, 10), Size.of(16, 16))
						.setToggleState(linkedContainer.powered[j]);
				powerSwitch.setUuid(linkedContainer.uuids[j]);
				powerSwitch.setContainer(getContainer());
				panel.createChild(WStaticText::new, Position.of(panel, 20, 18, 10), Size.of(100, 8))
						.setText(Formatting.GRAY + linkedContainer.uuids[j].toString().split("-", 2)[0]);
				panel.createChild(WStaticText::new, Position.ofTopRight(panel), Size.of(100, 16))
						.setText((linkedContainer.claimSize > j ? Formatting.RED : Formatting.GREEN) + String.valueOf(linkedContainer.powers[j]));
				energyNetScroll.addRow(panel);
			}
			energyNetScroll.setOffsetY(offset - 0.2f);
			energyNetScroll.scroll(0,0.2);
		}
	}
}

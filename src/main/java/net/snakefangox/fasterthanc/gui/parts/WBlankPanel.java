package net.snakefangox.fasterthanc.gui.parts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.api.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class WBlankPanel extends WAbstractWidget implements WModifiableCollection, WDelegatedEventListener {
	protected Set<WAbstractWidget> widgets = new LinkedHashSet<>();

	public WBlankPanel() {
	}

	public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
		if (!this.isHidden()) {
			Iterator var8 = this.widgets.iterator();

			while(var8.hasNext()) {
				WLayoutElement widget = (WLayoutElement)var8.next();
				widget.draw(matrices, provider);
			}
		}
	}

	@Override
	public void add(WAbstractWidget... widgets) {
		this.widgets.addAll(Arrays.asList(widgets));
		onLayoutChange();
	}

	@Override
	public Set<WAbstractWidget> getWidgets() {
		return widgets;
	}

	@Override
	public boolean contains(WAbstractWidget... widgets) {
		return this.widgets.containsAll(Arrays.asList(widgets));
	}

	@Override
	public void remove(WAbstractWidget... widgets) {
		this.widgets.removeAll(Arrays.asList(widgets));
		onLayoutChange();
	}

	@Override
	public Collection<? extends WEventListener> getEventDelegates() {
		return getWidgets();
	}
}

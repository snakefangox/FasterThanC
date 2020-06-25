package net.snakefangox.fasterthanc.gui.parts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.api.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class WBlankPanel extends WAbstractWidget implements WModifiableCollection, WDrawableCollection, WDelegatedEventListener {
	protected Set<WAbstractWidget> heldWidgets = new LinkedHashSet();
	protected List<WLayoutElement> orderedWidgets = new ArrayList();
	private int index = 0;

	public WBlankPanel() {
	}

	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
		if (!this.isHidden()) {
			Iterator var8 = this.getOrderedWidgets().iterator();

			while(var8.hasNext()) {
				WLayoutElement widget = (WLayoutElement)var8.next();
				widget.draw(matrices, provider);
			}

		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void add(WAbstractWidget... widgets) {
		this.heldWidgets.addAll(Arrays.asList(widgets));
		this.onLayoutChange();
	}

	public void onLayoutChange() {
		super.onLayoutChange();
		this.recalculateCache();
	}

	public void recalculateCache() {
		this.orderedWidgets = new ArrayList(this.getWidgets());
		Collections.sort(this.orderedWidgets);
		Collections.reverse(this.orderedWidgets);
	}

	public List<WLayoutElement> getOrderedWidgets() {
		return this.orderedWidgets;
	}

	public Set<WAbstractWidget> getWidgets() {
		return this.heldWidgets;
	}

	public boolean contains(WAbstractWidget... widgets) {
		return this.heldWidgets.containsAll(Arrays.asList(widgets));
	}

	public void remove(WAbstractWidget... widgets) {
		this.heldWidgets.removeAll(Arrays.asList(widgets));
		this.onLayoutChange();
	}

	public Collection<? extends WEventListener> getEventDelegates() {
		return this.getWidgets();
	}
}

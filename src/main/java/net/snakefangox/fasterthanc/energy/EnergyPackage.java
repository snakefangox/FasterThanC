package net.snakefangox.fasterthanc.energy;

import net.minecraft.text.Text;

public class EnergyPackage {
	int amount;
	public final Text name;
	private int remainingPulls = 4;

	public EnergyPackage(int amount, Text name) {
		this.amount = amount;
		this.name = name;
	}

	public void refresh() {
		remainingPulls = 4;
	}

	public void pull() {
		if (remainingPulls > 0) {
			--remainingPulls;
		}
	}

	public boolean getShouldRemove() {
		return remainingPulls <= 0;
	}

	public int getAmount() {
		return amount;
	}
}
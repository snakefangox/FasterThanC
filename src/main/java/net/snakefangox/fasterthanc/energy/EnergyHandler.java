package net.snakefangox.fasterthanc.energy;

import java.util.*;

public interface EnergyHandler extends Energy {

	Map<UUID, EnergyPackage> getProviders();
	Map<UUID, EnergyPackage> getClaimants();
	Set<UUID> getPoweredDown();

	/**
	 * Provide an amount of energy to the network
	 *
	 * @param uuid
	 * @param energyPackage
	 */
	default void provideEnergy(UUID uuid, EnergyPackage energyPackage) {
		if (getPoweredDown().contains(uuid)) {
			energyPackage.amount = 0;
			getProviders().put(uuid, energyPackage);
			return;
		}
		if (getProviders().containsKey(uuid)) {
			if (energyPackage.amount == getProviders().get(uuid).amount) {
				getProviders().get(uuid).refresh();
			} else {
				getProviders().put(uuid, energyPackage);
			}
		} else {
			getProviders().put(uuid, energyPackage);
		}
	}

	/**
	 * Attempt to claim an amount of energy from the network
	 *
	 * @param uuid
	 * @param energyPackage
	 * @return if the energy is available
	 */
	default boolean claimEnergy(UUID uuid, EnergyPackage energyPackage) {
		if (getPoweredDown().contains(uuid)) {
			energyPackage.amount = 0;
			getClaimants().put(uuid, energyPackage);
			return false;
		}
		if (getClaimants().containsKey(uuid)) {
			if (energyPackage.amount == getClaimants().get(uuid).amount) {
				getClaimants().get(uuid).refresh();
				return true;
			} else {
				if (sumProvided() - (sumClaimed() - getClaimants().get(uuid).amount) >= energyPackage.amount) {
					getClaimants().put(uuid, energyPackage);
					return true;
				} else {
					energyPackage.amount = 0;
					getClaimants().put(uuid, energyPackage);
					return false;
				}
			}

		} else {
			if (sumProvided() - sumClaimed() >= energyPackage.amount) {
				getClaimants().put(uuid, energyPackage);
				return true;
			} else {
				energyPackage.amount = 0;
				getClaimants().put(uuid, energyPackage);
				return false;
			}
		}
	}

	default void energyTick() {
		pullAll();
		int surplus = sumProvided() - sumClaimed();
		if (surplus < 0)
			getClaimants().clear();
	}

	default void pullAll() {
		Set<UUID> toRemove = new HashSet<>();
		for (Map.Entry<UUID, EnergyPackage> providers : getProviders().entrySet()) {
			providers.getValue().pull();
			if (providers.getValue().getShouldRemove()) {
				toRemove.add(providers.getKey());
			}
		}
		toRemove.forEach(id -> getProviders().remove(id));
		toRemove.clear();

		for (Map.Entry<UUID, EnergyPackage> claimants : getClaimants().entrySet()) {
			claimants.getValue().pull();
			if (claimants.getValue().getShouldRemove()) {
				toRemove.add(claimants.getKey());
			}
		}
		toRemove.forEach(id -> getClaimants().remove(id));
	}

	default int sumProvided() {
		int amount = 0;
		for (Map.Entry<UUID, EnergyPackage> providers : getProviders().entrySet()) {
			amount += providers.getValue().amount;
		}
		return amount;
	}

	default int sumClaimed() {
		int amount = 0;
		for (Map.Entry<UUID, EnergyPackage> claimants : getClaimants().entrySet()) {
			amount += claimants.getValue().amount;
		}
		return amount;
	}
}

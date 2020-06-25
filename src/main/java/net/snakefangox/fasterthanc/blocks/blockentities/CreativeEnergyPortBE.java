package net.snakefangox.fasterthanc.blocks.blockentities;

import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.templates.EnergyBE;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;

public class CreativeEnergyPortBE extends EnergyBE {
	public CreativeEnergyPortBE() {
		super(FRegister.creative_energy_port_type);
	}

	@Override
	public void onEnergy(EnergyHandler be) {
		be.provideEnergy(getEnergyID(), new EnergyPackage(1000, "Creative Energy Port"));
	}
}

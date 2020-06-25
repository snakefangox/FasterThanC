package net.snakefangox.fasterthanc.energy;

public interface EnergyConsumer extends Energy {
	default int getEnergy(){return 0;}
	default int getEnergyAvailable() {return 0;}
}

package net.snakefangox.fasterthanc.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.EnergyManagementComputerBE;
import spinnery.common.container.BaseContainer;
import spinnery.widget.WSlot;

import java.util.UUID;

public class EnergyComputerContainer extends BaseContainer {

	public final PlayerEntity player;
	public final EnergyManagementComputerBE controllerBE;

	public UUID[] uuids = new UUID[0];
	public String[] names = new String[0];
	public int[] powers = new int[0];
	public int claimSize;
	public boolean[] powered = new boolean[0];
	public boolean shouldUpdate = false;
	private int sendUpdates = 60;

	public EnergyComputerContainer(int synchronizationID, PlayerInventory playerInventory, EnergyManagementComputerBE reactorControllerBE) {
		super(synchronizationID, playerInventory);
		this.player = playerInventory.player;
		this.controllerBE = reactorControllerBE;
		WSlot.addHeadlessPlayerInventory(getInterface());
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		if (sendUpdates > 0){
			--sendUpdates;
		}else {
			sendUpdates = 60;
			FRegister.energy_management_computer.sendEnergyNetToPlayer(controllerBE.getPos(), controllerBE.getWorld(), player);
		}
	}
}

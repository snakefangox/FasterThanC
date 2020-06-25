package net.snakefangox.fasterthanc.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.EnergyManagementComputerBE;
import net.snakefangox.fasterthanc.blocks.blockentities.TargetingComputerBE;
import spinnery.common.container.BaseContainer;
import spinnery.widget.WSlot;

import java.util.UUID;

public class TargetingComputerContainer extends BaseContainer {

	public final PlayerEntity player;
	public final TargetingComputerBE controllerBE;

	public UUID[] uuids = new UUID[0];
	public String[] names = new String[0];
	public float[] pitch = new float[0];
	public float[] yaw = new float[0];
	public boolean shouldUpdate = false;
	private int sendUpdates = 1200;

	public TargetingComputerContainer(int synchronizationID, PlayerInventory playerInventory, TargetingComputerBE reactorControllerBE) {
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
			sendUpdates = 1200;
			FRegister.targeting_computer.sendHardPointsToPlayer(controllerBE.getPos(), controllerBE.getWorld(), player);
		}
	}
}

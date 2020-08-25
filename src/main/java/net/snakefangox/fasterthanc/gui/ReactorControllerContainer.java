package net.snakefangox.fasterthanc.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorControllerBE;
import spinnery.common.container.BaseContainer;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WSlot;

public class ReactorControllerContainer extends BaseScreenHandler {

	public final PlayerEntity player;
	public final ReactorControllerBE controllerBE;
	public boolean complete = false;
	public int chambers = 0;
	public int remainingFuel = 0;
	public boolean shouldUpdate = false;
	private int sendUpdates = 20;

	public ReactorControllerContainer(int synchronizationID, PlayerInventory playerInventory, ReactorControllerBE reactorControllerBE) {
		super(synchronizationID, playerInventory);
		//TODO REMOVE THIS
		type = FRegister.reactor_container;
		this.player = playerInventory.player;
		this.controllerBE = reactorControllerBE;
		WSlot.addHeadlessPlayerInventory(getInterface());
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		if (sendUpdates > 0){
			--sendUpdates;
		} else {
			sendUpdates = 20;
			FRegister.reactor_controller.sendReactorDataToPlayer(controllerBE.getPos(), controllerBE.getWorld(), player);
		}
	}
}

package net.snakefangox.fasterthanc.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.JumpDriveControllerBE;
import spinnery.common.container.BaseContainer;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class JumpDriveControllerContainer extends BaseContainer {
	public static final int INV = 1;

	public final PlayerEntity player;
	public final JumpDriveControllerBE controllerBE;
	public boolean complete = false;
	public int chambers = 0;
	public boolean shouldUpdate = false;
	public ItemStack stack;
	private int sendUpdates = 20;

	public JumpDriveControllerContainer(int synchronizationID, PlayerInventory playerInventory, JumpDriveControllerBE jumpDriveControllerBE) {
		super(synchronizationID, playerInventory);
		this.player = playerInventory.player;
		controllerBE = jumpDriveControllerBE;

		WInterface wInterface = getInterface();
		getInventories().put(INV, jumpDriveControllerBE);

		WSlot.addHeadlessArray(wInterface, 0, INV, 1, 1);
		WSlot.addHeadlessPlayerInventory(wInterface);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		if (sendUpdates > 0){
			--sendUpdates;
		}else {
			sendUpdates = 20;
			FRegister.jump_drive_controller.sendJumpDriveDataToPlayer(controllerBE.getPos(), controllerBE.getWorld(), player);
		}
	}
}

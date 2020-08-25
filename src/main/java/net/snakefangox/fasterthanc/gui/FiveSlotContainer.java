package net.snakefangox.fasterthanc.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;

import net.snakefangox.fasterthanc.FRegister;
import spinnery.common.container.BaseContainer;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.api.WNetworked;

public class FiveSlotContainer extends BaseScreenHandler {
	public static final int INV = 1;

	public final PlayerEntity player;

	public FiveSlotContainer(int synchronizationID, PlayerInventory playerInventory, Inventory inventory) {
		super(synchronizationID, playerInventory);
		//TODO REMOVE THIS
		type = FRegister.five_slot_container;
		this.player = playerInventory.player;

		WInterface wInterface = getInterface();
		getInventories().put(INV, inventory);

		WSlot.addHeadlessArray(wInterface, 0, INV, 5, 1);
		WSlot.addHeadlessPlayerInventory(wInterface);
	}

	@Override
	public void onInterfaceEvent(int widgetSyncId, WNetworked.Event event, CompoundTag payload) {
		super.onInterfaceEvent(widgetSyncId, event, payload);
	}
}

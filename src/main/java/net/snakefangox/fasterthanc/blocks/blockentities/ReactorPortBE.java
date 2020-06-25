package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.ReactorPort;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

public class ReactorPortBE extends BlockEntity implements SimpleInventory, Tickable {

	private static final int OP_FREQ = 20;
	DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(5, ItemStack.EMPTY);
	BlockPos controllerOffset = null;
	ReactorPort.PortType type = null;

	public ReactorPortBE() {
		super(FRegister.reactor_port_type);
	}

	@Override
	public void tick() {
		if (world.getTime() % OP_FREQ == 0) {
			if (type == null) {
				type = world.getBlockState(pos).getBlock() == FRegister.reactor_input ? ReactorPort.PortType.IN : ReactorPort.PortType.OUT;
			}
			if (controllerOffset != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos.add(controllerOffset));
				if (blockEntity instanceof ReactorControllerBE) {
					if (type == ReactorPort.PortType.IN) {
						int amountToInsert = 0;
						int slot = 0;
						for (int i = 0; i < itemStacks.size(); i++) {
							if (itemStacks.get(i).getItem() == FRegister.fuel_cell) {
								amountToInsert += itemStacks.get(i).getCount();
								slot = i;
								break;
							}
						}
						if (amountToInsert > 0) {
							int taken = ((ReactorControllerBE) blockEntity).insertFuel(amountToInsert, true);
							removeStack(slot, taken);
						}
					} else {
						int space = 0;
						int slot = 0;
						for (int i = 0; i < itemStacks.size(); i++) {
							if (itemStacks.get(i).getItem() == FRegister.empty_fuel_cell || itemStacks.get(i).isEmpty()) {
								space = FRegister.fuel_cell.getMaxCount() - itemStacks.get(i).getCount();
								slot = i;
								break;
							}
						}
						if (space > 0) {
							ItemStack result = ((ReactorControllerBE) blockEntity).takeCells(space);
							setStack(slot, new ItemStack(result.getItem(), itemStacks.get(slot).getCount() + result.getCount()));
						}
					}
				}
			}
		}
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Inventories.fromTag(tag, itemStacks);
		if (tag.contains("controllerOffset")) {
			int[] off = tag.getIntArray("controllerOffset");
			controllerOffset = new BlockPos(off[0], off[1], off[2]);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, itemStacks);
		if (controllerOffset != null)
			tag.putIntArray("controllerOffset", new int[]{controllerOffset.getX(), controllerOffset.getY(), controllerOffset.getZ()});
		return super.toTag(tag);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return itemStacks;
	}

	public void setControllerOffset(BlockPos controllerOffset) {
		this.controllerOffset = controllerOffset;
	}
}

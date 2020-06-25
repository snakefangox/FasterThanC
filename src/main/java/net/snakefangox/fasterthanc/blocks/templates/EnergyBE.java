package net.snakefangox.fasterthanc.blocks.templates;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.energy.EnergyHandler;

import java.util.UUID;

public abstract class EnergyBE extends BlockEntity implements Energy, Tickable {

	UUID energyID;

	public EnergyBE(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void tick() {
		if (!world.isClient && world.getTime() % Energy.ENERGY_TICK == 0) {
			if (energyID == null)
				energyID = UUID.randomUUID();
			for (Direction dir : Direction.values()) {
				BlockEntity be = world.getBlockEntity(pos.offset(dir));
				if (be instanceof EnergyHandler) {
					onEnergy((EnergyHandler) be);
					break;
				}
			}
		}
	}
	public abstract void onEnergy(EnergyHandler be);

	public UUID getEnergyID() {
		return energyID;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		if (energyID != null)
			tag.putUuid("energyID", energyID);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if (tag.contains("energyID"))
			energyID = tag.getUuid("energyID");
	}
}

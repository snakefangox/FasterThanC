package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;

import java.util.UUID;

public class SolarPanelBE extends BlockEntity implements Energy, Tickable {

	private static final String NAME = "Solar Panel";
	UUID energyID;

	public SolarPanelBE() {
		super(FRegister.solar_panel_type);
	}

	@Override
	public void tick() {
		if (world.isClient()) return;
		if (world.getTime() % Energy.ENERGY_TICK == 0) {
			if (energyID == null)
				energyID = UUID.randomUUID();
			for (Direction dir : Direction.values()) {
				BlockEntity be = world.getBlockEntity(pos.offset(dir));
				if (be instanceof EnergyHandler) {
					((EnergyHandler) be).provideEnergy(energyID, new EnergyPackage((world.getSkyAngle(0) > 0.7 || world.getSkyAngle(0) < 0.25) ? 1 : 0, NAME));
					break;
				}
			}
		}
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

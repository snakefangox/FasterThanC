package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.energy.EnergyHandler;

import java.util.UUID;

public class EnergyManagementComputerBE extends BlockEntity implements Energy {

	public EnergyManagementComputerBE() {
		super(FRegister.energy_management_computer_type);
	}

	public void powerChange(UUID uuid, boolean powerState, PlayerEntity playerEntity){
		EnergyHandler energyHandler = null;
		for (Direction dir : Direction.values()) {
			BlockPos blockPos = pos.offset(dir);
			assert world != null;
			BlockEntity be = world.getBlockEntity(blockPos);
			if (be instanceof EnergyHandler) {
				energyHandler = (EnergyHandler) be;
			}
		}
		if (powerState && energyHandler != null) {
			energyHandler.getPoweredDown().add(uuid);
		} else {
			assert energyHandler != null;
			energyHandler.getPoweredDown().remove(uuid);
		}
		FRegister.energy_management_computer.sendEnergyNetToPlayer(pos, world, playerEntity);
	}
}

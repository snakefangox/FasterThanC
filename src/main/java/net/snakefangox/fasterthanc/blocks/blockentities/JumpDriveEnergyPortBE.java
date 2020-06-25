package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.entity.BlockEntity;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.EnergyConsumer;

public class JumpDriveEnergyPortBE extends BlockEntity implements EnergyConsumer {
	public JumpDriveEnergyPortBE() {
		super(FRegister.jump_drive_energy_port_type);
	}
}

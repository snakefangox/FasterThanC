package net.snakefangox.fasterthanc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.snakefangox.fasterthanc.FRegister;

public class ReactorEnergyPort extends Block implements BlockEntityProvider {
	public ReactorEnergyPort(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.reactor_energy_port_type.instantiate();
	}
}

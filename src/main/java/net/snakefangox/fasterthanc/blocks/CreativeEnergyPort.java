package net.snakefangox.fasterthanc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.snakefangox.fasterthanc.FRegister;

public class CreativeEnergyPort extends Block implements BlockEntityProvider {
	public CreativeEnergyPort(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.creative_energy_port_type.instantiate();
	}
}

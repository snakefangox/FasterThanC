package net.snakefangox.fasterthanc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.snakefangox.fasterthanc.FRegister;

public class SolarPanel extends Block implements BlockEntityProvider {
	public SolarPanel(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.solar_panel_type.instantiate();
	}
}

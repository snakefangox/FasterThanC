package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.Energy;

public class ReactorEnergyPortBE extends BlockEntity implements Energy {

	BlockPos controllerOffset = null;

	public ReactorEnergyPortBE() {
		super(FRegister.reactor_energy_port_type);
	}

	public void setControllerOffset(BlockPos subtract) {
		controllerOffset = subtract;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if (tag.contains("controllerOffset")) {
			int[] off = tag.getIntArray("controllerOffset");
			controllerOffset = new BlockPos(off[0], off[1], off[2]);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		if (controllerOffset != null)
			tag.putIntArray("controllerOffset", new int[]{controllerOffset.getX(), controllerOffset.getY(), controllerOffset.getZ()});
		return super.toTag(tag);
	}
}

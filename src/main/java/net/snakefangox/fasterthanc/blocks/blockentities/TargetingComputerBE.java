package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.Hardpoint;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.tasks.ScanCableNetwork;

public class TargetingComputerBE extends BlockEntity implements Energy, ScanCableNetwork.ReturnAddress, Tickable {

	private BlockPos[] hardPoints = new BlockPos[0];

	public TargetingComputerBE() {
		super(FRegister.targeting_computer_type);
	}

	@Override
	public void returnFindings(BlockPos... posl) {
		hardPoints = posl;
	}

	public BlockPos[] getHardPoints() {
		return hardPoints;
	}

	@Override
	public void tick() {
		if (world.getTime() % 21 == 0 && !world.isClient) {
			OvertimeManager.instantRunTask(new ScanCableNetwork(pos, FRegister.hardpoint, false, this, world), world.getServer());
		}
	}

	public void fireHardpoint(int id) {
		if (id < hardPoints.length) {
			BlockEntity be = world.getBlockEntity(hardPoints[id]);
			if (be instanceof HardpointBE && be.getCachedState().get(Hardpoint.DEPLOYED)) {
				((HardpointBE)be).fire();
			}
		}
	}

	public void tiltHardpoint(int id, float pitch, float yaw) {
		if (id < hardPoints.length) {
			BlockEntity be = world.getBlockEntity(hardPoints[id]);
			if (be instanceof HardpointBE) {
				((HardpointBE)be).setAim(pitch, yaw);
			}
		}
	}
}

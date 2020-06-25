package net.snakefangox.fasterthanc.overtime.tasks;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.JumpDriveControllerBE;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import java.util.ArrayList;
import java.util.List;

public class ScanJumpDrive implements OvertimeTask {

	public final List<BlockPos> toScan = new ArrayList<>();
	public BlockPos energyPort = null;
	int chamberCount = 0;
	int index = 0;
	final JumpDriveControllerBE returnAddress;

	public ScanJumpDrive(JumpDriveControllerBE returnAddress) {
		this.returnAddress = returnAddress;
		toScan.add(returnAddress.getPos());
	}

	@Override
	public void process(MinecraftServer server) {
		BlockPos scanPos = toScan.get(index++);
		boolean mustSurround = false;
		if (returnAddress.getWorld().getBlockState(scanPos).getBlock() == FRegister.jump_drive_field_chamber)
			mustSurround = true;
		for (Direction dir : Direction.values()) {
			BlockPos scanPosOffset = scanPos.offset(dir);
			if (!returnAddress.getWorld().isAir(scanPosOffset) && !toScan.contains(scanPosOffset)) {
				Block block = returnAddress.getWorld().getBlockState(scanPosOffset).getBlock();
				if (block == FRegister.jump_drive_casing) {
					toScan.add(scanPosOffset);
				} else if (block == FRegister.jump_drive_field_chamber) {
					toScan.add(scanPosOffset);
					++chamberCount;
				} else if (block == FRegister.jump_drive_energy_port) {
					toScan.add(scanPosOffset);
					if (energyPort == null) {
						energyPort = scanPosOffset.subtract(returnAddress.getPos());
					} else {
						makeInvalid();
					}
				} else if (block == FRegister.jump_drive_controller || (mustSurround && block != FRegister.reactor_controller)) {
					makeInvalid();
				}
			} else if (mustSurround && returnAddress.getWorld().isAir(scanPosOffset)) {
				makeInvalid();
			}
		}
	}

	private void makeInvalid() {
		toScan.clear();
		energyPort = null;
		index = Integer.MAX_VALUE;
	}

	@Override
	public boolean isFinished() {
		boolean finished = index >= toScan.size();
		if (finished) {
			returnAddress.returnScanResults(index == toScan.size(), energyPort, chamberCount);
		}
		return finished;
	}

	@Override
	public boolean mustBeFinished() {
		return false;
	}
}

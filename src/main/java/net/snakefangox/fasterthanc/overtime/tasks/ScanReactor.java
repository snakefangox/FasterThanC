package net.snakefangox.fasterthanc.overtime.tasks;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorControllerBE;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorEnergyPortBE;
import net.snakefangox.fasterthanc.blocks.blockentities.ReactorPortBE;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import java.util.ArrayList;
import java.util.List;

public class ScanReactor implements OvertimeTask {

	public static final BlockPos[] scanOffsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
			new BlockPos(1, 0, 1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, -1), new BlockPos(0, 1, 0),
			new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1),
			new BlockPos(1, 1, 1), new BlockPos(-1, 1, 1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, -1), new BlockPos(0, -1, 0),
			new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
			new BlockPos(1, -1, 1), new BlockPos(-1, -1, 1), new BlockPos(1, -1, -1), new BlockPos(-1, -1, -1)};

	public final List<BlockPos> toScan = new ArrayList<>();
	public final List<BlockPos> tanks = new ArrayList<>();
	public BlockPos energyPort = null;
	int chamberCount = 0;
	int index = 0;
	final ReactorControllerBE returnAddress;

	public ScanReactor(ReactorControllerBE returnAddress) {
		this.returnAddress = returnAddress;
		toScan.add(returnAddress.getPos());
	}

	@Override
	public void process(MinecraftServer server) {
		BlockPos scanPos = toScan.get(index++);
		boolean mustSurround = false;
		if (returnAddress.getWorld().getBlockState(scanPos).getBlock() == FRegister.reactor_tank)
			mustSurround = true;
		for (BlockPos bp : scanOffsets) {
			BlockPos scanPosOffset = scanPos.add(bp);
			if (!returnAddress.getWorld().isAir(scanPosOffset) && !toScan.contains(scanPosOffset)) {
				Block block = returnAddress.getWorld().getBlockState(scanPosOffset).getBlock();
				if (block == FRegister.reactor_casing || block == FRegister.reactor_glass) {
					toScan.add(scanPosOffset);
				} else if (block == FRegister.reactor_tank) {
					tanks.add(scanPosOffset.subtract(returnAddress.getPos()));
					toScan.add(scanPosOffset);
				} else if (block == FRegister.reactor_chamber) {
					toScan.add(scanPosOffset);
					++chamberCount;
				} else if (block == FRegister.reactor_input || block == FRegister.reactor_output) {
					toScan.add(scanPosOffset);
					if (returnAddress.getWorld().getBlockEntity(scanPosOffset) instanceof ReactorPortBE)
						((ReactorPortBE) returnAddress.getWorld().getBlockEntity(scanPosOffset)).setControllerOffset(returnAddress.getPos().subtract(scanPosOffset));
				} else if (block == FRegister.reactor_energy_port) {
					toScan.add(scanPosOffset);
					if (energyPort == null) {
						energyPort = scanPosOffset.subtract(returnAddress.getPos());
						if (returnAddress.getWorld().getBlockEntity(scanPosOffset) instanceof ReactorEnergyPortBE)
							((ReactorEnergyPortBE) returnAddress.getWorld().getBlockEntity(scanPosOffset)).setControllerOffset(returnAddress.getPos().subtract(scanPosOffset));
					} else {
						makeInvalid();
					}
				} else if (block == FRegister.reactor_controller || (mustSurround && block != FRegister.reactor_controller)) {
					makeInvalid();
				}
			} else if (mustSurround && returnAddress.getWorld().isAir(scanPosOffset)) {
				makeInvalid();
			}
		}
	}

	private void makeInvalid() {
		toScan.clear();
		tanks.clear();
		energyPort = null;
	}

	@Override
	public boolean isFinished() {
		boolean finished = index >= toScan.size();
		if (finished) {
			returnAddress.returnScanResults(index == toScan.size(), tanks, energyPort, chamberCount);
		}
		return finished;
	}

	@Override
	public boolean mustBeFinished() {
		return false;
	}
}

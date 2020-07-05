package net.snakefangox.fasterthanc.overtime.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.blockentities.JumpDriveControllerBE;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class ScanShip implements OvertimeTask {

	public static final BlockPos[] scanOffsets = new BlockPos[] {new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
			new BlockPos(1, 0, 1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, -1), new BlockPos(0, 1, 0),
			new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1),
			new BlockPos(1, 1, 1), new BlockPos(-1, 1, 1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, -1), new BlockPos(0, -1, 0),
			new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
			new BlockPos(1, -1, 1), new BlockPos(-1, -1, 1), new BlockPos(1, -1, -1), new BlockPos(-1, -1, -1)};
	private static final int SHIP_LIMIT = 7000;
	private static final Set<Block> SKIP_OVER;

	static {
		SKIP_OVER = new HashSet<>();
		SKIP_OVER.add(FRegister.jump_beacon);
		SKIP_OVER.add(FRegister.jump_breaker);
		SKIP_OVER.add(Blocks.BEDROCK);
	}

	private final List<BlockPos> toScan = new ArrayList<>();
	private int index = 0;
	final JumpDriveControllerBE returnAddress;

	public ScanShip(JumpDriveControllerBE returnAddress) {
		this.returnAddress = returnAddress;
		toScan.add(returnAddress.getPos());
	}

	@Override
	public void process(MinecraftServer server) {
		BlockPos pos = toScan.get(index++);
		for (BlockPos offset : scanOffsets) {
			BlockPos scanPos = pos.add(offset);
			BlockState state = returnAddress.getWorld().getBlockState(scanPos);
			if (!returnAddress.getWorld().isAir(scanPos) && !toScan.contains(scanPos) && !SKIP_OVER.contains(state.getBlock())) {
				toScan.add(scanPos);
			}
		}
	}

	@Override
	public boolean isFinished() {
		boolean finished = index >= toScan.size();
		if (finished) {
			returnAddress.returnShipScanResults(toScan);
		}
		return finished || index >= SHIP_LIMIT;
	}

	@Override
	public boolean mustBeFinished() {
		return false;
	}
}

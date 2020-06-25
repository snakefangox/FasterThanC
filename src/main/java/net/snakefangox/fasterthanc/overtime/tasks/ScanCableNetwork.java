package net.snakefangox.fasterthanc.overtime.tasks;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import java.util.ArrayList;
import java.util.List;

public class ScanCableNetwork implements OvertimeTask {
	Block searchingFor;
	boolean endOnFirst;
	ReturnAddress returnAddress;
	World world;
	List<BlockPos> scan = new ArrayList<BlockPos>();
	List<BlockPos> result = new ArrayList<BlockPos>();
	int index = 0;

	public ScanCableNetwork(BlockPos startPos, Block searchingFor, boolean endOnFirst, ReturnAddress returnAddress, World world) {
		scan.add(startPos);
		this.searchingFor = searchingFor;
		this.endOnFirst = endOnFirst;
		this.returnAddress = returnAddress;
		this.world = world;
	}

	@Override
	public void process(MinecraftServer server) {
		for (Direction dir : Direction.values()) {
			BlockPos off = scan.get(index).offset(dir);
			if (world.getBlockState(off).getBlock() == FRegister.high_capacity_cable && !scan.contains(off)) {
				scan.add(off);
			} else if (world.getBlockState(off).getBlock() == searchingFor) {
				result.add(off);
			}
		}
		++index;
	}

	@Override
	public boolean isFinished() {
		boolean finished = scan.size() == index || (!result.isEmpty() && endOnFirst);
		if (finished) {
			returnAddress.returnFindings(result.toArray(new BlockPos[result.size()]));
		}
		return finished;
	}

	@Override
	public boolean mustBeFinished() {
		return false;
	}

	public interface ReturnAddress {
		void returnFindings(BlockPos... pos);
	}
}

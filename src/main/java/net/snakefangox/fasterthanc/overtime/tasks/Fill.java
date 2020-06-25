package net.snakefangox.fasterthanc.overtime.tasks;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.overtime.OvertimeTask;

import java.util.ArrayList;
import java.util.List;

public class Fill implements OvertimeTask {
	Block fill;
	World world;
	List<BlockPos> swap = new ArrayList<BlockPos>();
	int index = 0;
	final int limit;

	public Fill(World world, BlockPos pos, Block fill, int limit) {
		this.world = world;
		this.fill = fill;
		swap.add(pos);
		this.limit = limit;
	}

	@Override
	public void process(MinecraftServer server) {
		world.setBlockState(swap.get(index), fill.getDefaultState());
		for (Direction dir : Direction.values()) {
			BlockPos off = swap.get(index).offset(dir);
			if (!world.isAir(off) && !swap.contains(off))
				swap.add(off);
		}
		++index;
	}

	@Override
	public boolean isFinished() {
		return swap.size() == index || index > limit;
	}

	@Override
	public boolean mustBeFinished() {
		return false;
	}
}

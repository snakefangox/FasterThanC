package net.snakefangox.fasterthanc.tools;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

public class AdjGraph extends Graph<BlockPos> {

	public void add(BlockPos pos) {
		addVertex(pos);
		for (Direction dir : Direction.values()) {
			BlockPos offset = pos.offset(dir);
			if (hasVertex(offset))
				addEdge(pos, offset);
		}
	}

	private void DFS(BlockPos pos, Set<BlockPos> searched) {
		searched.add(pos);
		for (BlockPos off : verticesMap.get(pos)) {
			if (!searched.contains(off))
				DFS(off, searched);
		}
	}

	public Set<Set<BlockPos>> splitDFS(BlockPos splitPoint) {
		Set<Set<BlockPos>> foundNetworks = new HashSet<>();
		for (BlockPos pos : verticesMap.get(splitPoint)) {
			for (Set<BlockPos> set : foundNetworks) if (set.contains(pos)) continue;
			Set<BlockPos> searched = new HashSet<>();
			searched.add(splitPoint);
			DFS(pos, searched);
			searched.remove(splitPoint);
			foundNetworks.add(searched);
		}
		return foundNetworks;
	}
}

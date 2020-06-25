package net.snakefangox.fasterthanc.energy;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.snakefangox.fasterthanc.FasterThanC;
import net.snakefangox.fasterthanc.tools.AdjGraph;
import net.snakefangox.fasterthanc.tools.Graph;

import java.util.*;

public class CableNetworkStorage extends PersistentState {

	public static final String KEY = FasterThanC.MODID + "cable_network";

	List<CableNetwork> cable_networks = new ArrayList<CableNetwork>();
	int nextID = 0;

	public CableNetworkStorage() {
		super(KEY);
	}

	public CableNetwork createNewCableNetwork(BlockPos pos, World world) {
		CableNetwork cn = new CableNetwork();
		cn.addCable(pos, this);
		int index = getNextID();
		// Shouldn't actually need the index here but better safe then sorry
		cable_networks.add(index, cn);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NetworkMember) {
			((NetworkMember) be).setNetwork(index);
		}
		return cn;
	}

	@Nullable
	public CableNetwork getCableNetwork(int id) {
		if (id >= 0 && id < cable_networks.size())
			return cable_networks.get(id);
		return null;
	}

	public CableNetwork getOrCreateCableNetwork(int id, BlockPos pos, World world) {
		if (id >= 0 && id < cable_networks.size())
			return cable_networks.get(id);
		return createNewCableNetwork(pos, world);
	}

	public void addToCableNetwork(int id, BlockPos pos, World world) {
		CableNetwork cn = getCableNetwork(id);
		if (cn != null)
			cn.addCable(pos, this);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof NetworkMember)
			((NetworkMember) be).setNetwork(id);
	}

	public void mergeCableNetworks(int id1, int id2, World world) {
		CableNetwork cn1 = getCableNetwork(id1);
		CableNetwork cn2 = getCableNetwork(id2);
		if (cn1 != null && cn2 != null) {
			cn1.appendNetwork(cn2.getNetwork());
			for (BlockPos pos : cn2.network) {
				BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof NetworkMember) {
					((NetworkMember) be).setNetwork(id1);
				}
			}
			cable_networks.set(id2, null);
			markDirty();
		}
	}

	public void removeCableFromNetwork(int id, BlockPos splitPoint, WorldAccess world) {
		CableNetwork cn = getCableNetwork(id);
		if (cn != null) {
			Set<Set<BlockPos>> splitNetworks = cn.splitNetwork(splitPoint);
			cable_networks.set(id, null);
			for (Set<BlockPos> netToBe : splitNetworks) {
				CableNetwork cableNetwork = new CableNetwork();
				cableNetwork.appendNetwork(netToBe);
				int index = getNextID();
				cable_networks.add(index, cableNetwork);
				for (BlockPos cable : netToBe) {
					BlockEntity be = world.getBlockEntity(cable);
					if (be instanceof NetworkMember)
						((NetworkMember) be).setNetwork(index);
				}
			}
			cable_networks.remove(cn);
		}
	}

	public int getNextID() {
		return nextID++;
	}

	public static CableNetworkStorage getInstance(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(() -> new CableNetworkStorage(), KEY);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		nextID = tag.getInt("nextID");
		for (int i = 0; i < nextID; i++) {
			String s = String.valueOf(i);
			if (tag.contains(s)) {
				CableNetwork network = new CableNetwork();
				network.fromTag(tag.getCompound(s));
				cable_networks.add(i, network);
			} else {
				cable_networks.add(i, null);
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		for (int i = 0; i < cable_networks.size(); i++) {
			CableNetwork cn = cable_networks.get(i);
			if (cn != null)
				tag.put(String.valueOf(i), cn.toTag(new CompoundTag()));
		}
		tag.putInt("nextID", nextID);
		return tag;
	}

	@Override
	public String toString() {
		String value = "";
		int nonNullCount = 0;
		int count = 0;
		for (CableNetwork net : cable_networks) {
			count++;
			if (net != null) {
				value += "Index:" + count + " " + net.toString() + " \n ";
				nonNullCount++;
			}
		}
		return value + " \n Contains:" + nonNullCount;
	}

	public static void tickPipes(World world) {
		if (world.getTime() % Energy.ENERGY_TICK == 0 && world instanceof ServerWorld)
			for (CableNetwork network : getInstance((ServerWorld) world).cable_networks) {
				if (network != null) {
					network.energyTick();
				}
			}
	}

	public static class CableNetwork implements EnergyHandler {

		private AdjGraph network = new AdjGraph();
		private final Map<UUID, EnergyPackage> providers = new HashMap<>();
		private final Map<UUID, EnergyPackage> claimants = new HashMap<>();
		private final Set<UUID> poweredOff = new HashSet<>();


		public void addCable(BlockPos pos, CableNetworkStorage instance) {
			network.add(pos);
			instance.markDirty();
		}

		public void appendNetwork(Graph<BlockPos> appNetwork) {
			for (BlockPos pos : appNetwork) {
				network.add(pos);
			}
		}

		public void appendNetwork(Set<BlockPos> appNetwork) {
			for (BlockPos pos : appNetwork) {
				network.add(pos);
			}
		}

		public void removeCable(BlockPos pos, CableNetworkStorage instance) {
			network.removeVertex(pos);
			instance.markDirty();
		}

		@Override
		public Map<UUID, EnergyPackage> getProviders() {
			return providers;
		}

		@Override
		public Map<UUID, EnergyPackage> getClaimants() {
			return claimants;
		}

		@Override
		public Set<UUID> getPoweredDown() {	return poweredOff; }

		public boolean contains(BlockPos pos) {
			return network.hasVertex(pos);
		}

		public Graph<BlockPos> getNetwork() {
			return network;
		}

		public Set<Set<BlockPos>> splitNetwork(BlockPos splitPoint) {
			return network.splitDFS(splitPoint);
		}

		public void fromTag(CompoundTag tag) {
			CompoundTag setTag = tag.getCompound("set");
			for (String index : setTag.getKeys()) {
				network.add(BlockPos.fromLong(setTag.getLong(index)));
			}
			CompoundTag poweredTag = tag.getCompound("powered");
			for (String index : poweredTag.getKeys()) {
				poweredOff.add(poweredTag.getUuid(index));
			}
		}

		public CompoundTag toTag(CompoundTag tag) {
			CompoundTag setTag = new CompoundTag();
			Iterator<BlockPos> iterator = network.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				setTag.putLong(String.valueOf(i++), iterator.next().asLong());
			}
			tag.put("set", setTag);
			CompoundTag poweredTag = new CompoundTag();
			Iterator<UUID> iteratorR = poweredOff.iterator();
			i = 0;
			while (iteratorR.hasNext()) {
				poweredTag.putUuid(String.valueOf(i++), iteratorR.next());
			}
			tag.put("powered", poweredTag);
			return tag;
		}

		@Override
		public String toString() {
			return network.toString();
		}
	}

	public interface NetworkMember {
		void setNetwork(int id);

		int getNetwork();
	}
}

package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.CableNetworkStorage;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HighCapacityCableBE extends BlockEntity implements EnergyHandler, CableNetworkStorage.NetworkMember {
	int networkID;

	public HighCapacityCableBE() {
		super(FRegister.high_capacity_cable_type);
	}

	@Override
	public Map<UUID, EnergyPackage> getProviders() {
		if (world != null) {
			return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getProviders();
		} else {
			return new HashMap<>();
		}
	}

	@Override
	public Map<UUID, EnergyPackage> getClaimants() {
		if (world != null) {
			return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getClaimants();
		} else {
			return new HashMap<>();
		}
	}

	@Override
	public Set<UUID> getPoweredDown() {
		if (world != null) {
			return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getPoweredDown();
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public void provideEnergy(UUID uuid, EnergyPackage energyPackage) {
		if (world != null) {
			CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).provideEnergy(uuid, energyPackage);
		}
	}

	@Override
	public boolean claimEnergy(UUID uuid, EnergyPackage energyPackage) {
		if (world != null) {
			return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).claimEnergy(uuid, energyPackage);
		} else {
			return false;
		}
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		networkID = tag.getInt("networkID");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("networkID", networkID);
		return super.toTag(tag);
	}

	@Override
	public void setNetwork(int id) {
		networkID = id;
	}

	@Override
	public int getNetwork() {
		return networkID;
	}
}

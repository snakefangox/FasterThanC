package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.CableNetworkStorage;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;

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
		return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getProviders();
	}

	@Override
	public Map<UUID, EnergyPackage> getClaimants() {
		return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getClaimants();
	}

	@Override
	public Set<UUID> getPoweredDown() {
		return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).getPoweredDown();
	}

	@Override
	public void provideEnergy(UUID uuid, EnergyPackage energyPackage) {
		try {
			CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).provideEnergy(uuid, energyPackage);
		}catch (NullPointerException p){}
	}

	@Override
	public boolean claimEnergy(UUID uuid, EnergyPackage energyPackage) {
		try {
		return CableNetworkStorage.getInstance((ServerWorld) world).getOrCreateCableNetwork(networkID, pos, world).claimEnergy(uuid, energyPackage);
		}catch (NullPointerException p){}
		return false;
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

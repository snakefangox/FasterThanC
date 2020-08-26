package net.snakefangox.fasterthanc.blocks.blockentities;

import java.util.Random;

import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.tools.SimpleInventory;
import net.snakefangox.fasterthanc.worldgen.CrashedShipFeature;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

public class DeorbitorBE extends BlockEntity implements SimpleInventory, BlockEntityClientSerializable, Tickable {

	private static final int MAX_CALL_TIME = 200;
	private static final CrashedShipFeature SHIP_DEBRIS = new CrashedShipFeature(DefaultFeatureConfig.CODEC);

	DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
	public int callTime = -1;

	public DeorbitorBE() {
		super(FRegister.deorbiter_type);
	}

	public void startCall() {
		if (callTime == -1) {
			callTime = MAX_CALL_TIME;
			checkValid();
			sync();
		}
		if (callTime != -1) {
			world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void tick() {
		if (callTime > -1) {
			--callTime;
			if (!world.isClient()) {
				if (callTime == MAX_CALL_TIME / 2) {
					breakMagnet();
				} else if (callTime > MAX_CALL_TIME / 2) {
					checkValid();
				} else if (callTime == -1) {
					doCall();
				} else if (callTime % 20 == 0) {
					world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
					for (int i = 0; i < world.getHeight(); ++i) {
						((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + i, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
					}
				}
			}
		}
	}

	private void checkValid() {
		if (itemStacks.get(0).isEmpty() || itemStacks.get(0).getItem() != FRegister.hyper_magnet) callTime = -1;
	}

	private void breakMagnet() {
		itemStacks.set(0, ItemStack.EMPTY);
		sync();
		world.playSound(null, pos, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		((ServerWorld) world).spawnParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 1.8, pos.getZ() + 0.5, 1, 0, -0.1, 0, 0.1);
	}

	private void doCall() {
		world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 15.0F, true, Explosion.DestructionType.DESTROY);
		Random random = new Random();
		SHIP_DEBRIS.generate((StructureWorldAccess) world, null, random, pos, null);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return itemStacks;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Inventories.fromTag(tag, itemStacks);
		callTime = tag.getInt("callTime");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, itemStacks);
		tag.putInt("callTime", callTime);
		return super.toTag(tag);
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
		Inventories.fromTag(compoundTag, stacks);
		itemStacks = stacks;
		callTime = compoundTag.getInt("callTime");
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag) {
		Inventories.toTag(compoundTag, itemStacks, true);
		compoundTag.putInt("callTime", callTime);
		return compoundTag;
	}
}

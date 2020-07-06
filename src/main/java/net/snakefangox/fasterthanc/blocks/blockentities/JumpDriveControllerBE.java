package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;
import net.snakefangox.fasterthanc.items.BeaconCoordsStorage;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.tasks.Jump;
import net.snakefangox.fasterthanc.overtime.tasks.ScanCableNetwork;
import net.snakefangox.fasterthanc.overtime.tasks.ScanJumpDrive;
import net.snakefangox.fasterthanc.overtime.tasks.ScanShip;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JumpDriveControllerBE extends BlockEntity implements SimpleInventory, Tickable, ScanCableNetwork.ReturnAddress {

	public static final int SCAN_FREQ = 200;
	public static final int JUMP_COUNTDOWN = 6 * 20 - 10;
	public static final int MAX_BLIND_JUMP = 100000;
	DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
	public static Text name = new TranslatableText("text.fasterthanc.jump_drive");
	public boolean isComplete = false;
	public boolean powered = false;
	BlockPos energyPort = null;
	public int chambers = 0;
	UUID energyID;
	List<BlockPos> shipPositions = new ArrayList<>();
	int jumpCountdown = -1;
	boolean fuel = false;

	public JumpDriveControllerBE() {
		super(FRegister.jump_drive_controller_type);
	}

	@Override
	public void tick() {
		if (world.isClient()) return;
		if (world.getTime() % SCAN_FREQ == 0) {
			OvertimeManager.addTask(new ScanJumpDrive(this));
		}
		if (world.getTime() % Energy.ENERGY_TICK == 0 && isComplete && energyPort != null) {
			if (energyID == null)
				energyID = UUID.randomUUID();
			BlockPos port = pos.add(energyPort);
			for (Direction dir : Direction.values()) {
				BlockEntity be = world.getBlockEntity(port.offset(dir));
				if (be instanceof EnergyHandler) {
					powered = ((EnergyHandler) be).claimEnergy(energyID, new EnergyPackage(chambers, name));
					break;
				}
			}
		}
		if (jumpCountdown > -1) {
			if (jumpCountdown > 0) {
				--jumpCountdown;
			} else {
				jumpCountdown = -1;
				jump();
			}
		}
	}

	public void jump() {
		if (energyPort != null && powered && isComplete) {
			BlockPos port = pos.add(energyPort);
			OvertimeManager.instantRunTask(new ScanCableNetwork(port, FRegister.reactor_energy_port, true, this, world), world.getServer());
			if (!fuel)return;
			fuel = false;
			ItemStack stack = getStack(0);
			if (stack.getItem() instanceof BeaconCoordsStorage) {
				BlockPos coords = BeaconCoordsStorage.getPos(stack);
				RegistryKey<World> dim = BeaconCoordsStorage.getDim(stack);
				OvertimeManager.addTask(new Jump(shipPositions, coords.subtract(pos), world, dim));
			} else {
				OvertimeManager.addTask(new Jump(shipPositions, new BlockPos(
						(MAX_BLIND_JUMP / 2) - world.getRandom().nextInt(MAX_BLIND_JUMP),
						0,
						(MAX_BLIND_JUMP / 2) - world.getRandom().nextInt(MAX_BLIND_JUMP)), world, BeaconCoordsStorage.getDim(stack)));
			}
		}
	}

	public void startJump() {
		if (jumpCountdown == -1 && !world.isClient && powered) {
			OvertimeManager.addTask(new ScanShip(this));
			jumpCountdown = JUMP_COUNTDOWN;
			world.playSound(null, pos, FRegister.JUMP_DRIVE_SPOOLS, SoundCategory.BLOCKS, 10, 1);
		}
	}

	public void returnShipScanResults(List<BlockPos> toScan) {
		shipPositions = toScan;
	}

	public void returnScanResults(boolean b, BlockPos energyPort, int chamberCount) {
		isComplete = b;
		this.energyPort = energyPort;
		this.chambers = chamberCount;
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return itemStacks;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Inventories.fromTag(tag, itemStacks);
		if (tag.contains("energyID"))
			energyID = tag.getUuid("energyID");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, itemStacks);
		if (energyID != null)
			tag.putUuid("energyID", energyID);
		return super.toTag(tag);
	}

	@Override
	public void returnFindings(BlockPos... blockPos) {
		if (blockPos.length > 0 && world.getBlockEntity(blockPos[0]) instanceof ReactorEnergyPortBE) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos[0].add(((ReactorEnergyPortBE)world.getBlockEntity(blockPos[0])).controllerOffset));
			if (blockEntity instanceof ReactorControllerBE) {
				fuel =  ((ReactorControllerBE)blockEntity).consumeFuel();
			}
		}
	}
}

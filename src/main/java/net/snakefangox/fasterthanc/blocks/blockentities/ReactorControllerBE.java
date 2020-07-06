package net.snakefangox.fasterthanc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.ReactorTank;
import net.snakefangox.fasterthanc.energy.Energy;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.tasks.ScanReactor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReactorControllerBE extends BlockEntity implements Tickable {

	public static final int SCAN_FREQ = 200;
	public static final int TICKS_PER_FUEL = 36000;
	public boolean isComplete = false;
	public static Text name = new TranslatableText("text.fasterthanc.reactor");
	List<BlockPos> tanks = new ArrayList<>();
	BlockPos energyPort = null;
	public int chambers = 0;
	int spareCells = 0;
	public int remainingFuelTicks = 0;
	int remainingCooldownTicks = 0;
	UUID energyID;

	public ReactorControllerBE() {
		super(FRegister.reactor_controller_type);
	}

	@Override
	public void tick() {
		if (world.isClient()) return;
		if (world.getTime() % SCAN_FREQ == 0) {
			OvertimeManager.addTask(new ScanReactor(this));
		}
		if (isComplete) {
			if (remainingFuelTicks > 0) {
				--remainingFuelTicks;
			} else if (remainingCooldownTicks <= 0) {
				boolean fuel = consumeFuel();
				if (fuel) {
					remainingFuelTicks = TICKS_PER_FUEL;
				} else {
					remainingCooldownTicks = 40;
				}
			} else {
				--remainingCooldownTicks;
			}
			if (world.getTime() % Energy.ENERGY_TICK == 0 && remainingFuelTicks > 0 && energyPort != null) {
				if (energyID == null)
					energyID = UUID.randomUUID();
				BlockPos port = pos.add(energyPort);
				for (Direction dir : Direction.values()) {
					BlockEntity be = world.getBlockEntity(port.offset(dir));
					if (be instanceof EnergyHandler) {
						((EnergyHandler) be).provideEnergy(energyID, new EnergyPackage(chambers, name));
						break;
					}
				}
			}
		}
	}

	public void returnScanResults(boolean valid, List<BlockPos> tanks, BlockPos energyPort, int chambers) {
		this.tanks = tanks;
		this.energyPort = energyPort;
		this.chambers = chambers;
		isComplete = valid;
	}

	public int insertFuel(int maxCells, boolean returnCells) {
		int fillCount = maxCells;
		for (BlockPos bp : tanks) {
			if (fillCount == 0) break;
			BlockPos tankPos = bp.add(pos);
			BlockState state = world.getBlockState(tankPos);
			if (state.getBlock() == FRegister.reactor_tank && state.get(ReactorTank.FILLED) == false) {
				world.setBlockState(tankPos, state.with(ReactorTank.FILLED, true));
				--fillCount;
			}
		}
		if (returnCells) spareCells += maxCells - fillCount;
		return maxCells - fillCount;
	}

	public boolean consumeFuel() {
		for (BlockPos bp : tanks) {
			BlockPos tankPos = bp.add(pos);
			BlockState state = world.getBlockState(tankPos);
			if (state.getBlock() == FRegister.reactor_tank && state.get(ReactorTank.FILLED) == true) {
				world.setBlockState(tankPos, state.with(ReactorTank.FILLED, false));
				return true;
			}
		}
		return false;
	}

	public ItemStack takeCells(int maxTake) {
		int takeAmount = Math.min(spareCells, Math.min(maxTake, 16));
		spareCells -= takeAmount;
		ItemStack stack = new ItemStack(FRegister.empty_fuel_cell, takeAmount);
		return stack;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("spareCells", spareCells);
		tag.putInt("remainingFuelTicks", remainingFuelTicks);
		if (energyID != null)
			tag.putUuid("energyID", energyID);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		spareCells = tag.getInt("spareCells");
		remainingFuelTicks = tag.getInt("remainingFuelTicks");
		if (tag.contains("energyID"))
			energyID = tag.getUuid("energyID");
	}
}

package net.snakefangox.fasterthanc.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.snakefangox.fasterthanc.energy.EnergyHandler;

public class EnergyMeter extends Item {

	public EnergyMeter(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
			if (blockEntity instanceof EnergyHandler) {
				int energy = ((EnergyHandler)blockEntity).sumProvided();
				int taken = ((EnergyHandler)blockEntity).sumClaimed();
				context.getPlayer().sendMessage(new LiteralText("Energy: " + energy + ", Using: " + taken), true);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
}

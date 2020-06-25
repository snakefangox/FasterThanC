package net.snakefangox.fasterthanc.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.overtime.OvertimeManager;
import net.snakefangox.fasterthanc.overtime.tasks.Fill;

import java.util.List;

public class DebugTool extends Item {

	public DebugTool(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient) {
			OvertimeManager.addTask(new Fill(context.getWorld(), context.getBlockPos(), context.getPlayer().isSneaking() ? Blocks.AIR : Blocks.GOLD_BLOCK, 30000));
		}
		return ActionResult.SUCCESS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new LiteralText("His food and daughter met the same fate"));
		tooltip.add(new LiteralText("What you're about to do is a terrible idea"));
		tooltip.add(new LiteralText(Formatting.STRIKETHROUGH + "And no this isn't a vore stick"));
	}
}

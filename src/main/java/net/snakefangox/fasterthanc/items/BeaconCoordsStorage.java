package net.snakefangox.fasterthanc.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public class BeaconCoordsStorage extends Item {

	private static final String TAG_NAME = "beacon-coords";
	private static final String COORD_NAME = "coords";
	private static final String DIM_NAME = "dim";
	private static final String DIM_NAMESPACE = "dimn";

	public BeaconCoordsStorage(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasTag() && stack.getTag().contains(TAG_NAME)) {
			CompoundTag tag = stack.getTag().getCompound(TAG_NAME);
			int[] coords = tag.getIntArray(COORD_NAME);
			String dimName = tag.getString(DIM_NAME);
			tooltip.add(1, new LiteralText("Coords: " + coords[0] + ", " + coords[1] + ", " + coords[2]));
			tooltip.add(2, new LiteralText("Celestial Body: " + dimName));
		} else {
			tooltip.add(1, new LiteralText("WARNING: Missing location data"));
			tooltip.add(2, new LiteralText("don't use me"));
		}
	}

	public static void addCoords(ItemStack stack, BlockPos pos, World world) {
		CompoundTag tag = stack.getOrCreateSubTag(TAG_NAME);
		tag.putIntArray(COORD_NAME, new int[]{pos.getX(), pos.getY(), pos.getZ()});
		tag.putString(DIM_NAME, world.getRegistryKey().getValue().getPath());
		tag.putString(DIM_NAMESPACE, world.getRegistryKey().getValue().getNamespace());
	}

	public static RegistryKey<World> getDim(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains(TAG_NAME)) {
			CompoundTag tag = stack.getOrCreateSubTag(TAG_NAME);
			String path = tag.getString(DIM_NAME);
			String namespace = tag.getString(DIM_NAMESPACE);
			RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, new Identifier(namespace, path));
			return key;
		}
		return RegistryKey.of(Registry.DIMENSION, new Identifier("overworld"));
	}

	public static BlockPos getPos(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains(TAG_NAME)) {
			CompoundTag tag = stack.getOrCreateSubTag(TAG_NAME);
			int[] arrPos = tag.getIntArray(COORD_NAME);
			return new BlockPos(arrPos[0], arrPos[1], arrPos[2]);
		}
		return new BlockPos(0,0,0);
	}
}

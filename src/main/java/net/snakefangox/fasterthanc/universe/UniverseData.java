package net.snakefangox.fasterthanc.universe;

import java.util.ArrayList;
import java.util.List;

import net.snakefangox.fasterthanc.FasterThanC;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class UniverseData extends PersistentState {

	public static final String KEY = FasterThanC.MODID + "universe_data";

	public final List<Identifier> sectors = new ArrayList<>();

	public UniverseData() {
		super(KEY);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		CompoundTag stag = tag.getCompound("sectors");
		for (String s : tag.getKeys()) {
			sectors.add(new Identifier(stag.getString(s)));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		CompoundTag stag = new CompoundTag();
		tag.put("sectors", stag);
		int i = 0;
		for (Identifier entry : sectors) {
			stag.putString(String.valueOf(i++), entry.toString());
		}
		return tag;
	}

	public static UniverseData getInstance(MinecraftServer server) {
		return server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(UniverseData::new, KEY);
	}
}

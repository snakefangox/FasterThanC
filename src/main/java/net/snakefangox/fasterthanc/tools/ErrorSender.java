package net.snakefangox.fasterthanc.tools;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ErrorSender {

	public static void notifyError(World world, BlockPos pos, String error) {
		Box box = new Box(pos).expand(15,5,15);
		List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, box, EntityPredicates.VALID_ENTITY);
		players.forEach(p -> p.sendMessage(new LiteralText(Formatting.RED + "ERROR: " + Formatting.WHITE + error), true));
	}
}

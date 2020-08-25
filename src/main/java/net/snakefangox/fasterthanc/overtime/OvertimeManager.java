package net.snakefangox.fasterthanc.overtime;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;

public class OvertimeManager {

	private static final List<OvertimeTask> queue = new ArrayList<OvertimeTask>();
	private static final int MAX_MILLIS_PER_TICK = 30;
	private static boolean locked = false;

	public static void serverTick(MinecraftServer minecraftServer) {
		if (!locked) {
			long startTime = System.currentTimeMillis();
			boolean remainingTime = true;
			while (remainingTime && !queue.isEmpty()) {
				OvertimeTask current = queue.get(0);
				current.process(minecraftServer);
				if (current.isFinished()) {
					queue.remove(0);
				}
				remainingTime = System.currentTimeMillis() - startTime < MAX_MILLIS_PER_TICK;
			}
		}
	}

	public static void ServerClosing(MinecraftServer minecraftServer) {
		locked = true;
		for (OvertimeTask task : queue) {
			if (task.mustBeFinished()) {
				while (!task.isFinished()) {
					task.process(minecraftServer);
				}
			}
		}
		locked = false;
	}

	public static void addTask(OvertimeTask task) {
		queue.add(task);
	}

	public static void instantRunTask(OvertimeTask task, MinecraftServer server) {
		while (!task.isFinished()) task.process(server);
	}
}

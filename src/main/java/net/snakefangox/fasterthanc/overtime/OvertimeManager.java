package net.snakefangox.fasterthanc.overtime;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class OvertimeManager {

	private static final List<OvertimeTask> queue = new ArrayList<OvertimeTask>();
	private static final int MAX_MILLIS_PER_TICK = 30;

	public static void serverTick(MinecraftServer minecraftServer) {
		long startTime = System.currentTimeMillis();
		boolean remainingTime = true;
		while (remainingTime && !queue.isEmpty()) {
			OvertimeTask current = queue.get(0);
			current.process(minecraftServer);
			if (current.isFinished())
				queue.remove(0);
			remainingTime = System.currentTimeMillis() - startTime < MAX_MILLIS_PER_TICK;
		}
	}

	public static void ServerClosing(MinecraftServer minecraftServer) {
		for (OvertimeTask task : queue) {
			if (task.mustBeFinished()) {
				while (!task.isFinished()) {
					task.process(minecraftServer);
				}
			}
		}
	}

	public static void addTask(OvertimeTask task) {
		queue.add(task);
	}

	public static void instantRunTask(OvertimeTask task, MinecraftServer server) {
		while (!task.isFinished()) task.process(server);
	}
}

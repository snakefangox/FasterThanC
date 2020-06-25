package net.snakefangox.fasterthanc.overtime;

import net.minecraft.server.MinecraftServer;

public interface OvertimeTask {
	/**
	 * Should be used to perform some small part of the overall task
	 *
	 * @param server
	 */
	void process(MinecraftServer server);

	/**
	 * Should return true when the task is finished
	 *
	 * @return
	 */
	boolean isFinished();

	/**
	 * Marks a task as vital, will be completed barring power loss
	 * @return
	 */
	boolean mustBeFinished();
}

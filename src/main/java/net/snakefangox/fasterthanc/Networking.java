package net.snakefangox.fasterthanc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.blocks.blockentities.EnergyManagementComputerBE;
import net.snakefangox.fasterthanc.blocks.blockentities.TargetingComputerBE;
import net.snakefangox.fasterthanc.gui.EnergyComputerContainer;
import net.snakefangox.fasterthanc.gui.JumpDriveControllerContainer;
import net.snakefangox.fasterthanc.gui.ReactorControllerContainer;
import net.snakefangox.fasterthanc.gui.TargetingComputerContainer;

import java.util.UUID;

public class Networking {
	//Packets
	public static final Identifier ENERGY_NET_TO_CLIENT = new Identifier(FasterThanC.MODID, "energy_to_client");
	public static final Identifier HARDPOINTS_TO_CLIENT = new Identifier(FasterThanC.MODID, "hardpoints_to_client");
	public static final Identifier ENERGY_NET_TO_SERVER = new Identifier(FasterThanC.MODID, "energy_to_server");
	public static final Identifier FIRE_TO_SERVER = new Identifier(FasterThanC.MODID, "fire_to_server");
	public static final Identifier ANZIMITH_TO_SERVER = new Identifier(FasterThanC.MODID, "anzimith_to_server");
	public static final Identifier REACTOR_DATA = new Identifier(FasterThanC.MODID, "reactor_data");
	public static final Identifier JUMP_DRIVE_DATA = new Identifier(FasterThanC.MODID, "jump_drive_data");

	public static void registerToServer() {
		ServerSidePacketRegistry.INSTANCE.register(ENERGY_NET_TO_SERVER, (packetContext, packetByteBuf) -> {
			BlockPos blockPos = packetByteBuf.readBlockPos();
			UUID uuid = packetByteBuf.readUuid();
			boolean power = packetByteBuf.readBoolean();
			packetContext.getTaskQueue().execute(() -> {
				World world = packetContext.getPlayer().world;
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof EnergyManagementComputerBE)
					((EnergyManagementComputerBE) blockEntity).powerChange(uuid, power, packetContext.getPlayer());
			});
		});
		ServerSidePacketRegistry.INSTANCE.register(FIRE_TO_SERVER, (packetContext, packetByteBuf) -> {
			BlockPos blockPos = packetByteBuf.readBlockPos();
			int id = packetByteBuf.readInt();
			packetContext.getTaskQueue().execute(() -> {
				World world = packetContext.getPlayer().world;
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof TargetingComputerBE)
					((TargetingComputerBE) blockEntity).fireHardpoint(id);
			});
		});
		ServerSidePacketRegistry.INSTANCE.register(ANZIMITH_TO_SERVER, (packetContext, packetByteBuf) -> {
			BlockPos blockPos = packetByteBuf.readBlockPos();
			int id = packetByteBuf.readInt();
			float pitch = packetByteBuf.readFloat();
			float yaw = packetByteBuf.readFloat();
			packetContext.getTaskQueue().execute(() -> {
				World world = packetContext.getPlayer().world;
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof TargetingComputerBE)
					((TargetingComputerBE) blockEntity).tiltHardpoint(id, pitch, yaw);
			});
		});
	}

	@Environment(EnvType.CLIENT)
	public static void registerToClient() {
		ClientSidePacketRegistry.INSTANCE.register(REACTOR_DATA,
				((packetContext, packetByteBuf) -> {
					boolean complete = packetByteBuf.readBoolean();
					int chambers = packetByteBuf.readInt();
					int remainingFuel = packetByteBuf.readInt();
					packetContext.getTaskQueue().execute(() -> {
						ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
						if (screenHandler instanceof ReactorControllerContainer) {
							ReactorControllerContainer ec = ((ReactorControllerContainer) MinecraftClient.getInstance().player.currentScreenHandler);
							ec.complete = complete;
							ec.chambers = chambers;
							ec.remainingFuel = remainingFuel;
							ec.shouldUpdate = true;
						}
					});
				}));
		ClientSidePacketRegistry.INSTANCE.register(JUMP_DRIVE_DATA,
				((packetContext, packetByteBuf) -> {
					boolean complete = packetByteBuf.readBoolean();
					int chambers = packetByteBuf.readInt();
					ItemStack stack = packetByteBuf.readItemStack();
					packetContext.getTaskQueue().execute(() -> {
						assert MinecraftClient.getInstance().player != null;
						ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
						if (screenHandler instanceof JumpDriveControllerContainer) {
							JumpDriveControllerContainer ec = ((JumpDriveControllerContainer) MinecraftClient.getInstance().player.currentScreenHandler);
							ec.complete = complete;
							ec.chambers = chambers;
							ec.stack = stack;
							ec.shouldUpdate = true;
						}
					});
				}));
		ClientSidePacketRegistry.INSTANCE.register(ENERGY_NET_TO_CLIENT,
				((packetContext, packetByteBuf) -> {
					int size = packetByteBuf.readInt();
					int claimSize = packetByteBuf.readInt();
					UUID[] uuids = new UUID[size];
					Text[] names = new Text[size];
					int[] powers = new int[size];
					boolean[] powered = new boolean[size];

					for (int i = 0; i < size; i++) {
						uuids[i] = packetByteBuf.readUuid();
						names[i] = packetByteBuf.readText();
						powers[i] = packetByteBuf.readInt();
						powered[i] = packetByteBuf.readBoolean();
					}
					packetContext.getTaskQueue().execute(() -> {
						ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
						if (screenHandler instanceof EnergyComputerContainer) {
							EnergyComputerContainer ec = ((EnergyComputerContainer) MinecraftClient.getInstance().player.currentScreenHandler);
							ec.names = names;
							ec.uuids = uuids;
							ec.powers = powers;
							ec.powered = powered;
							ec.claimSize = claimSize;
							ec.shouldUpdate = true;
						}
					});
				}));
		ClientSidePacketRegistry.INSTANCE.register(HARDPOINTS_TO_CLIENT,
				((packetContext, packetByteBuf) -> {
					int size = packetByteBuf.readInt();
					UUID[] uuids = new UUID[size];
					Text[] names = new Text[size];
					float[] pitch = new float[size];
					float[] yaw = new float[size];

					for (int i = 0; i < size; i++) {
						uuids[i] = packetByteBuf.readUuid();
						names[i] = packetByteBuf.readText();
						pitch[i] = packetByteBuf.readFloat();
						yaw[i] = packetByteBuf.readFloat();
					}
					packetContext.getTaskQueue().execute(() -> {
						ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
						if (screenHandler instanceof TargetingComputerContainer) {
							TargetingComputerContainer ec = ((TargetingComputerContainer) MinecraftClient.getInstance().player.currentScreenHandler);
							ec.names = names;
							ec.uuids = uuids;
							ec.pitch = pitch;
							ec.yaw = yaw;
							ec.shouldUpdate = true;
						}
					});
				}));
	}
}

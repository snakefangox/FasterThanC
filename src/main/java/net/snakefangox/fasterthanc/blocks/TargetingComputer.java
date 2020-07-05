package net.snakefangox.fasterthanc.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.Networking;
import net.snakefangox.fasterthanc.blocks.blockentities.HardpointBE;
import net.snakefangox.fasterthanc.blocks.blockentities.TargetingComputerBE;
import net.snakefangox.fasterthanc.blocks.templates.HorizontalRotatableBlock;

import java.util.UUID;

public class TargetingComputer extends HorizontalRotatableBlock implements BlockEntityProvider {
	public TargetingComputer(Settings settings) {
		super(settings, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			ContainerProviderRegistry.INSTANCE.openContainer(FRegister.targeting_computer_container, player, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
			sendHardPointsToPlayer(pos, world, player);
		}
		return ActionResult.SUCCESS;
	}

	public void sendHardPointsToPlayer(BlockPos pos, World world, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof TargetingComputerBE) {
			TargetingComputerBE be = (TargetingComputerBE) world.getBlockEntity(pos);
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			assert be != null;
			passedData.writeInt(be.getHardPoints().length);
			for (BlockPos hardpoint : be.getHardPoints()) {
				if (world.getBlockEntity(hardpoint) instanceof HardpointBE){
					HardpointBE hard = (HardpointBE) world.getBlockEntity(hardpoint);
					assert hard != null;
					passedData.writeUuid(hard.getEnergyID());
					passedData.writeText(hard.getName());
					passedData.writeFloat(hard.getPitch());
					passedData.writeFloat(hard.getYaw());
				} else {
					passedData.writeUuid(UUID.randomUUID());
					passedData.writeString("Invalid");
					passedData.writeFloat(0);
					passedData.writeFloat(0);
				}
			}
			ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Networking.HARDPOINTS_TO_CLIENT, passedData);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.targeting_computer_type.instantiate();
	}
}

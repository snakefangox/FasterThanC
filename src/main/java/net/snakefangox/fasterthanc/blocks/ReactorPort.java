package net.snakefangox.fasterthanc.blocks;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

public class ReactorPort extends Block implements BlockEntityProvider {

	final PortType portType;

	public ReactorPort(Settings settings, PortType portType) {
		super(settings);
		this.portType = portType;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return FRegister.reactor_port_type.instantiate();
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		if (world.getBlockEntity(pos) instanceof SimpleInventory && !world.isClient) {
			SimpleInventory be = (SimpleInventory) world.getBlockEntity(pos);
			for (ItemStack stack : be.getItems()) {
				ItemEntity ie = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(ie);
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			ContainerProviderRegistry.INSTANCE.openContainer(FRegister.five_slot_container, player, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
		}
		return ActionResult.SUCCESS;
	}

	public enum PortType {
		IN, OUT;
	}
}

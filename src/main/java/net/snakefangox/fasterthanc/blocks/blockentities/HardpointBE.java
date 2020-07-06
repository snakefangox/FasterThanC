package net.snakefangox.fasterthanc.blocks.blockentities;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.explosion.Explosion;
import net.snakefangox.fasterthanc.FRegister;
import net.snakefangox.fasterthanc.blocks.Hardpoint;
import net.snakefangox.fasterthanc.blocks.templates.EnergyBE;
import net.snakefangox.fasterthanc.energy.EnergyHandler;
import net.snakefangox.fasterthanc.energy.EnergyPackage;
import net.snakefangox.fasterthanc.tools.SimpleInventory;

public class HardpointBE extends EnergyBE implements SimpleInventory, BlockEntityClientSerializable {
	DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
	float pitch = 0;
	float yaw = 0;
	int cooldown = 0;
	boolean powered = false;

	public HardpointBE() {
		super(FRegister.hardpoint_type);
	}

	@Override
	public boolean canCableConnect(Direction dir) {
		return dir != Direction.UP;
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public void handleChanges() {
		sync();
	}

	@Override
	public void onEnergy(EnergyHandler be) {
		int amount = getCachedState().get(Hardpoint.DEPLOYED) ? 1 : 0;
		powered = be.claimEnergy(getEnergyID(), new EnergyPackage(amount, getName()));
		if (cooldown > 0) --cooldown;
	}

	public Text getName() {
		return new TranslatableText("text.fasterthanc.hardpoint", getStack(0).getName());
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Inventories.fromTag(tag, itemStacks);
		pitch = tag.getFloat("pitch");
		yaw = tag.getFloat("yaw");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, itemStacks);
		tag.putFloat("pitch", pitch);
		tag.putFloat("yaw", yaw);
		return super.toTag(tag);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return itemStacks;
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
		Inventories.fromTag(compoundTag, stacks);
		itemStacks = stacks;
		pitch = compoundTag.getFloat("pitch");
		yaw = compoundTag.getFloat("yaw");
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag) {
		Inventories.toTag(compoundTag, itemStacks, true);
		compoundTag.putFloat("pitch", pitch);
		compoundTag.putFloat("yaw", yaw);
		return compoundTag;
	}

	public void setAim(float pitch, float yaw) {
		this.pitch = pitch;
		this.yaw = yaw;
		sync();
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void fire() {
		if (powered && !getStack(0).isEmpty() && cooldown == 0) {
			cooldown = 3;
			Vec3d firingDirVec = getDirectionVector();
			Direction face = getCachedState().get(HorizontalFacingBlock.FACING);
			Direction firingDir = face == Direction.NORTH || face == Direction.SOUTH ? face : face.getOpposite();
			firingDirVec = firingDirVec.rotateY((float) Math.toRadians(firingDir.asRotation()));

			Vec3d firingPosVec = firingDirVec.multiply(120);
			firingPosVec = firingPosVec.add(pos.getX(), pos.getY(), pos.getZ());
			RayTraceContext rtc = new RayTraceContext(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5),
					firingPosVec, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.ANY,
					new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ()));
			assert world != null;
			BlockHitResult result = world.rayTrace(rtc);
			for (int i = 0; i < Math.sqrt(pos.getSquaredDistance(result.getBlockPos())); i++) {
				Vec3d part = firingDirVec.multiply(i);
				((ServerWorld) world).spawnParticles(ParticleTypes.FIREWORK, pos.getX() + 0.5 + part.x, pos.getY() + 1.5 + part.y, pos.getZ() + 0.5 + part.z,
						2, 0.0, 0.0, 0.0, 0);
			}
			world.playSound(null, pos, FRegister.LASER_FIRES, SoundCategory.BLOCKS, 4, 1);
			world.createExplosion(null, result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ(),
					5, Explosion.DestructionType.DESTROY);
		}
	}

	Vec3d getDirectionVector() {
		return new Vec3d(-Math.cos(pitch * Math.PI / 180.0) * Math.sin(yaw * Math.PI / 180.0),
				Math.sin(pitch * Math.PI / 180.0),
				-Math.cos(pitch * Math.PI / 180.0) * Math.cos(yaw * Math.PI / 180.0));
	}

}

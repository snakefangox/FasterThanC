package net.snakefangox.fasterthanc.blocks.blockentities.rendering;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.snakefangox.fasterthanc.blocks.Hardpoint;
import net.snakefangox.fasterthanc.blocks.blockentities.HardpointBE;

public class HardpointBER extends BlockEntityRenderer<HardpointBE> {

	public HardpointBER(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(HardpointBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.getCachedState().get(Hardpoint.DEPLOYED)) {
			matrices.push();
			Direction dir = entity.getCachedState().get(HorizontalFacingBlock.FACING);
			float pitch = entity.getPitch();
			float yaw = entity.getYaw();
			switch (dir){
				case NORTH:
					matrices.translate(0.5, 0.5, 0.85);
					rotate(matrices, pitch, yaw + dir.asRotation());
					break;
				case SOUTH:
					matrices.translate(0.5, 0.5, 0.15);
					rotate(matrices, pitch, yaw + dir.asRotation());
					break;
				case EAST:
					matrices.translate(0.15, 0.5, 0.5);
					rotate(matrices, pitch, yaw + dir.getOpposite().asRotation());
					break;
				case WEST:
					matrices.translate(0.85, 0.5, 0.5);
					rotate(matrices, pitch, yaw + dir.getOpposite().asRotation());
					break;
			}
			matrices.scale(5,5,5);
			MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getStack(0), ModelTransformation.Mode.GROUND,
					light, overlay, matrices, vertexConsumers);
			matrices.pop();
		}
	}

	private void rotate(MatrixStack matrices, float pitch, float yaw) {
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw));
	}
}

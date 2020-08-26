package net.snakefangox.fasterthanc.blocks.blockentities.rendering;

import net.snakefangox.fasterthanc.blocks.blockentities.DeorbitorBE;
import net.snakefangox.fasterthanc.blocks.blockentities.HardpointBE;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

public class DeorbitorBER extends BlockEntityRenderer<DeorbitorBE> {

	public DeorbitorBER(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(DeorbitorBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.translate(0.5, 1.8, 0.5);
		if (entity.callTime != -1)
			matrices.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(entity.callTime - tickDelta));
		MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getStack(0), ModelTransformation.Mode.GROUND,
						light, overlay, matrices, vertexConsumers);
		matrices.pop();
	}
}

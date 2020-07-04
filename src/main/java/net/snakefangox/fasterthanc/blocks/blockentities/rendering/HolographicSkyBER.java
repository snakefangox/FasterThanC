package net.snakefangox.fasterthanc.blocks.blockentities.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL30;

@Environment(EnvType.CLIENT)
public class HolographicSkyBER extends BlockEntityRenderer<BlockEntity> {
    private static final RenderPhase.Target SKY_TARGET = new RenderPhase.Target("fasterthanc_sky_target", () -> {
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(GL30.GL_TEXTURE0);
        WorldWithSky worldWithSky = (WorldWithSky) MinecraftClient.getInstance().worldRenderer;
        worldWithSky.getSky().beginRead();
    }, () -> {
        WorldWithSky worldWithSky = (WorldWithSky) MinecraftClient.getInstance().worldRenderer;
        worldWithSky.getSky().endRead();
        RenderSystem.disableTexture();
    });
    @SuppressWarnings("deprecation")
    private static final RenderPhase.Texturing SKY_TEXTURING = new RenderPhase.Texturing("fasterthanc_sky_texturing", () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.5F, 0.5F, 0.0F);
        RenderSystem.scalef(0.5F, 0.5F, 1.0F);
        RenderSystem.mulTextureByProjModelView();
        RenderSystem.matrixMode(5888);
        RenderSystem.setupEndPortalTexGen();
    }, () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.clearTexGen();
    });

    public interface WorldWithSky {
        Framebuffer getSky();
    }

    public HolographicSkyBER(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private RenderLayer getLayer() {
        return RenderLayer.of("fasterthanc_holographic_sky", VertexFormats.POSITION_COLOR, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().target(SKY_TARGET).texturing(SKY_TEXTURING).build(false));
    }

    public void render(BlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float g = getHeight();
        Matrix4f matrix4f = matrixStack.peek().getModel();
        renderLayer(g, matrix4f, vertexConsumerProvider.getBuffer(getLayer()));
    }

    private void renderLayer(float f, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float h = 1f;
        float i = 1f;
        float j = 1f;
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, h, i, j);
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, h, i, j);
        renderSide(matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j);
        renderSide(matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j);
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, h, i, j);
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 1.0F, 1.0F, 0.0F, 0.0F, h, i, j);
    }

    private void renderSide(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p) {
        vertexConsumer.vertex(matrix4f, f, h, j).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, h, k).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, i, l).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, f, i, m).color(n, o, p, 1.0F).next();
    }

    protected float getHeight() {
        return 1f;
    }
}

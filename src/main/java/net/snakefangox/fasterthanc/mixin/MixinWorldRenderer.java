package net.snakefangox.fasterthanc.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.snakefangox.fasterthanc.blocks.blockentities.rendering.HolographicSkyBER;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements HolographicSkyBER.WorldWithSky {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private Framebuffer sky;

    @Override
    public Framebuffer getSky() {
        return sky;
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=fog"), method = "render", allow = 1)
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        Framebuffer mcFb = client.getFramebuffer();

        if (sky == null) {
            sky = new Framebuffer(mcFb.viewportWidth, mcFb.viewportHeight, true, true);
        } else if (sky.viewportWidth != mcFb.viewportWidth || sky.viewportHeight != mcFb.viewportHeight) {
            sky.initFbo(mcFb.viewportWidth, mcFb.viewportHeight, true);
        }

        blit(mcFb, sky);

        mcFb.beginWrite(false);
    }

    @Unique
    private void blit(Framebuffer from, Framebuffer into) {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, into.fbo);
        GL30.glBlitFramebuffer(0, 0, from.viewportWidth, from.viewportHeight, 0, 0, into.viewportWidth, into.viewportHeight, GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }
}

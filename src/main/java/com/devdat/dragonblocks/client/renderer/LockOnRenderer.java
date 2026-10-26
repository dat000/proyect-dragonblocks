package com.devdat.dragonblocks.client.renderer;

import com.devdat.dragonblocks.client.input.LockOnManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = "dragonblocks", value = Dist.CLIENT)
public class LockOnRenderer {
    private static final ResourceLocation LOCK_TEXTURE = new ResourceLocation("dragonblocks", "textures/gui/lock_icon.png");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            Minecraft mc = Minecraft.getInstance();
            LivingEntity target = LockOnManager.getLockedTarget();

            if (target == null || mc.player == null) return;

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 camPos = camera.getPosition();

            // Posición interpolada del objetivo (centrado más arriba, sobre la cabeza)
            double partialTicks = event.getPartialTick();
            double x = target.xo + (target.getX() - target.xo) * partialTicks - camPos.x;
            double y = target.yo + (target.getY() - target.yo) * partialTicks + (target.getBbHeight() * 0.5D) - camPos.y; // Subido un poco más para que flote limpio
            double z = target.zo + (target.getZ() - target.zo) * partialTicks - camPos.z;

            poseStack.translate(x, y, z);

            // Efecto Billboard: El sprite rota automáticamente para mirar siempre a la cámara
            poseStack.mulPose(camera.rotation());

            // Escala del sprite en el mundo
            float scale = 0.6f;
            poseStack.scale(-scale, -scale, scale);

            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

            // USAMOS textSeeThrough PARA QUE SE RENDERICE SIEMPRE POR ENCIMA DE TODO
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.textSeeThrough(LOCK_TEXTURE));

            Matrix4f matrix4f = poseStack.last().pose();
            float size = 0.5f;

            vertexConsumer.vertex(matrix4f, -size, -size, 0.0f).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(0, 10).uv2(15728880).normal(0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -size, size, 0.0f).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(0, 10).uv2(15728880).normal(0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, size, size, 0.0f).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(0, 10).uv2(15728880).normal(0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, size, -size, 0.0f).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(0, 10).uv2(15728880).normal(0.0f, 1.0f, 0.0f).endVertex();

            bufferSource.endBatch();
            poseStack.popPose();
        }
    }
}
package com.devdat.dragonblocks.client.renderer;

import com.devdat.dragonblocks.client.model.RaceAnimatable;
import com.devdat.dragonblocks.client.model.RaceGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

// Dibuja a un jugador con el modelo GeckoLib de su raza
public class RacePlayerRenderer extends GeoReplacedEntityRenderer<AbstractClientPlayer, RaceAnimatable> {
    // Nombre del hueso de la cabeza en el .geo.json (debe llamarse igual en todos los modelos de raza)
    private static final String HEAD_BONE = "Head";

    public RacePlayerRenderer(EntityRendererProvider.Context context, RaceGeoModel model) {
        super(context, model, new RaceAnimatable());
        this.shadowRadius = 0.5f;
    }

    // La cabeza sigue la mirada del jugador (como el modelo vanilla). Se aplica solo en la matriz
    // de render, sin tocar la rotación del hueso: así no se acumula entre frames cuando la
    // animación actual (ej. "walk") no anima la cabeza, y se combina con la de "idle".
    @Override
    public void renderRecursively(PoseStack poseStack, RaceAnimatable animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        AbstractClientPlayer player = this.currentEntity;
        if (player == null || !HEAD_BONE.equals(bone.getName())) {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                    partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        float headYaw = Mth.rotLerp(partialTick, player.yHeadRotO, player.yHeadRot);
        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        float netHeadYaw = Mth.wrapDegrees(headYaw - bodyYaw);
        float headPitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());

        poseStack.pushPose();
        RenderUtils.translateToPivotPoint(poseStack, bone);
        // Signos negados: el modelo GeckoLib mira hacia -Z, mientras que en Minecraft el yaw crece
        // en sentido horario y el pitch positivo es mirar hacia abajo
        poseStack.mulPose(Axis.YP.rotationDegrees(-netHeadYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-headPitch));
        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}

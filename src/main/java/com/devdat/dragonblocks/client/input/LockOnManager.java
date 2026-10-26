package com.devdat.dragonblocks.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = "dragonblocks", value = Dist.CLIENT)
public class LockOnManager {
    private static LivingEntity lockedTarget = null;
    private static float currentYaw = 0.0f;
    private static float currentPitch = 0.0f;
    private static boolean initializedAngles = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            // Manejo de la tecla para activar/desactivar
            while (ClientKeybinds.LOCK_KEY.consumeClick()) {
                if (lockedTarget != null) {
                    lockedTarget = null;
                    initializedAngles = false;
                    mc.player.sendSystemMessage(Component.literal("§c[Lock-on] Desactivado"));
                } else {
                    lockedTarget = findBestTarget(mc);
                    if (lockedTarget != null) {
                        currentYaw = mc.player.getYRot();
                        currentPitch = mc.player.getXRot();
                        initializedAngles = true;
                        mc.player.sendSystemMessage(Component.literal("§a[Lock-on] Fijado en: " + lockedTarget.getName().getString()));
                    } else {
                        mc.player.sendSystemMessage(Component.literal("§e[Lock-on] Ningún objetivo cerca"));
                    }
                }
            }

            // Validar si el objetivo sigue vivo o se alejó demasiado
            if (lockedTarget != null) {
                if (!lockedTarget.isAlive() || lockedTarget.distanceToSqr(mc.player) > 400.0D) {
                    lockedTarget = null;
                    initializedAngles = false;
                    mc.player.sendSystemMessage(Component.literal("§c[Lock-on] Objetivo perdido"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || lockedTarget == null) return;

        if (!initializedAngles) {
            currentYaw = mc.player.getYRot();
            currentPitch = mc.player.getXRot();
            initializedAngles = true;
        }

        // Obtenemos el tick parcial para lograr fluidez a nivel de fotogramas (FPS)
        double partialTick = event.getPartialTick();
        Vec3 playerEyes = mc.player.getEyePosition((float) partialTick);

        // Posición interpolada del objetivo para evitar saltos
        double targetX = lockedTarget.xo + (lockedTarget.getX() - lockedTarget.xo) * partialTick;
        double targetY = lockedTarget.yo + (lockedTarget.getY() - lockedTarget.yo) * partialTick + lockedTarget.getEyeHeight() * 0.5D;
        double targetZ = lockedTarget.zo + (lockedTarget.getZ() - lockedTarget.zo) * partialTick;

        double dX = targetX - playerEyes.x;
        double dY = targetY - playerEyes.y;
        double dZ = targetZ - playerEyes.z;
        double distanceXZ = Math.sqrt(dX * dX + dZ * dZ);

        float targetYaw = (float) (Math.atan2(dZ, dX) * (180 / Math.PI)) - 90.0F;
        float targetPitch = (float) (-(Math.atan2(dY, distanceXZ) * (180 / Math.PI)));

        // Suavizado frame-rate independent
        currentYaw = lerpAngle(currentYaw, targetYaw, 0.25f);
        currentPitch = clamp(lerpAngle(currentPitch, targetPitch, 0.25f), -90.0F, 90.0F);

        // Aplicamos los ángulos directamente a la vista de la cámara
        event.setYaw(currentYaw);
        event.setPitch(currentPitch);

        // Sincronizamos la rotación visual del jugador
        mc.player.setYRot(currentYaw);
        mc.player.setXRot(currentPitch);
        mc.player.yHeadRot = currentYaw;
    }

    private static LivingEntity findBestTarget(Minecraft mc) {
        assert mc.player != null;
        assert mc.level != null;

        AABB searchBox = mc.player.getBoundingBox().inflate(16.0D);
        List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != mc.player && entity.isAlive());

        return entities.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(mc.player)))
                .orElse(null);
    }

    private static float lerpAngle(float current, float target, float factor) {
        float diff = target - current;
        while (diff < -180.0F) diff += 360.0F;
        while (diff >= 180.0F) diff -= 360.0F;
        return current + diff * factor;
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
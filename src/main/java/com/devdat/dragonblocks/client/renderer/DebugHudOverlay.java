package com.devdat.dragonblocks.client.renderer;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, value = Dist.CLIENT)
public class DebugHudOverlay {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        mc.player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
            GuiGraphics guiGraphics = event.getGuiGraphics();

            int x = 10;
            int y = 10;

            String kiText = "Ki: " + (int) stats.getKi() + " / " + (int) stats.getMaxKi();
            String staminaText = "Stamina: " + (int) stats.getStamina() + " / " + (int) stats.getMaxStamina();

            // Dibujamos el texto en pantalla con sombra
            guiGraphics.drawString(mc.font, kiText, x, y, 0xFFFF55, true);     // Amarillo brillante para el Ki
            guiGraphics.drawString(mc.font, staminaText, x, y + 12, 0x55FF55, true); // Verde brillante para la Estamina
        });
    }
}
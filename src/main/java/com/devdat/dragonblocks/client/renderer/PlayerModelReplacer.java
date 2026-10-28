package com.devdat.dragonblocks.client.renderer;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import com.devdat.dragonblocks.capability.Race;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Cancela el render vanilla del jugador y lo dibuja con el modelo GeckoLib de su raza
@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, value = Dist.CLIENT)
public class PlayerModelReplacer {

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player) || player.isSpectator()) return;

        player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
            RacePlayerRenderer renderer = RaceRenderers.get(Race.fromId(stats.getRace()));
            if (renderer == null) return;

            event.setCanceled(true);
            float partialTick = event.getPartialTick();
            float yaw = Mth.lerp(partialTick, player.yRotO, player.getYRot());
            renderer.render(player, yaw, partialTick, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        });
    }
}

package com.devdat.dragonblocks.client;

import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// Lógica de paquetes que solo existe en el cliente
public class ClientPacketHandler {

    public static void handleSyncRace(int entityId, int race) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(entityId);
        if (entity instanceof Player player) {
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> stats.setRace(race));
        }
    }
}

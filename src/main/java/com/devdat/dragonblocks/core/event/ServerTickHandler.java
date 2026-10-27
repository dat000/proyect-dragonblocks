package com.devdat.dragonblocks.core.event;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import com.devdat.dragonblocks.network.ModMessages;
import com.devdat.dragonblocks.network.SyncPlayerStatsPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.player;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
                if (stats.isCharging()) {
                    if (stats.getKi() < stats.getMaxKi()) {
                        stats.addKi(1.5f); // Sube el Ki

                        // Sincronizamos con el cliente
                        ModMessages.sendToPlayer(new SyncPlayerStatsPacket(
                                stats.getKi(), stats.getMaxKi(), stats.getStamina(), stats.getMaxStamina()
                        ), player);

                        // Debug en chat cada 1 segundo (20 ticks)
                        if (player.tickCount % 20 == 0) {
                            player.sendSystemMessage(Component.literal("§e[Ki] Cargando energía: " + (int)stats.getKi() + " / " + (int)stats.getMaxKi()));
                        }
                    }
                }
            });
        }
    }
}
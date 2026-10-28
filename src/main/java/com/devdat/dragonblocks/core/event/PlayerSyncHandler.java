package com.devdat.dragonblocks.core.event;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import com.devdat.dragonblocks.network.ModMessages;
import com.devdat.dragonblocks.network.SyncRacePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Mantiene la raza de cada jugador sincronizada con todos los clientes que lo ven
@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSyncHandler {

    // Envía la raza del jugador a él mismo y a todos los que lo están viendo
    public static void syncRace(ServerPlayer player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats ->
                ModMessages.sendToPlayerAndTrackers(new SyncRacePacket(player.getId(), stats.getRace()), player));
    }

    // Un jugador entra en el rango de visión de otro: le mandamos la raza del observado
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player target && event.getEntity() instanceof ServerPlayer tracker) {
            target.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats ->
                    ModMessages.sendToPlayer(new SyncRacePacket(target.getId(), stats.getRace()), tracker));
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRace(player);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRace(player);
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncRace(player);
    }

    // Al morir (o volver del End) se crea una entidad nueva: copiamos los datos para no perderlos
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        original.reviveCaps();
        original.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(oldStats ->
                event.getEntity().getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(newStats -> {
                    newStats.copyFrom(oldStats);
                    newStats.setCharging(false);
                }));
        original.invalidateCaps();
    }
}

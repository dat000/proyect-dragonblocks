package com.devdat.dragonblocks.network;

import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChargeKiPacket {
    private final boolean isCharging;

    public ChargeKiPacket(boolean isCharging) {
        this.isCharging = isCharging;
    }

    public ChargeKiPacket(FriendlyByteBuf buf) {
        this.isCharging = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isCharging);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                System.out.println("[DEBUG SERVIDOR] ¡Paquete recibido! Estado de carga: " + isCharging); // <-- Veremos esto en la consola
                player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
                    stats.setCharging(isCharging);
                });
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
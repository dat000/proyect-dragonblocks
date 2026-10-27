package com.devdat.dragonblocks.network;

import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPlayerStatsPacket {
    private final float ki;
    private final float maxKi;
    private final float stamina;
    private final float maxStamina;

    public SyncPlayerStatsPacket(float ki, float maxKi, float stamina, float maxStamina) {
        this.ki = ki;
        this.maxKi = maxKi;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
    }

    public SyncPlayerStatsPacket(FriendlyByteBuf buf) {
        this.ki = buf.readFloat();
        this.maxKi = buf.readFloat();
        this.stamina = buf.readFloat();
        this.maxStamina = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(ki);
        buf.writeFloat(maxKi);
        buf.writeFloat(stamina);
        buf.writeFloat(maxStamina);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> {
                    stats.setKi(ki);
                    stats.setMaxKi(maxKi);
                    stats.setStamina(stamina);
                    stats.setMaxStamina(maxStamina);
                });
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
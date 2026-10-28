package com.devdat.dragonblocks.network;

import com.devdat.dragonblocks.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Servidor -> Cliente: raza de un jugador (propio o ajeno) para elegir su modelo
public class SyncRacePacket {
    private final int entityId;
    private final int race;

    public SyncRacePacket(int entityId, int race) {
        this.entityId = entityId;
        this.race = race;
    }

    public SyncRacePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.race = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(race);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        // DistExecutor evita cargar clases de cliente en un servidor dedicado
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandler.handleSyncRace(entityId, race)));
        context.setPacketHandled(true);
        return true;
    }
}

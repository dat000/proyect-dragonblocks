package com.devdat.dragonblocks.network;

import com.devdat.dragonblocks.DragonBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(DragonBlocks.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // 1. Sincronización de Servidor a Cliente
        net.messageBuilder(SyncPlayerStatsPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncPlayerStatsPacket::new)
                .encoder(SyncPlayerStatsPacket::toBytes)
                .consumerNetworkThread(SyncPlayerStatsPacket::handle)
                .add();

        // 2. Cargar Ki de Cliente a Servidor (¡Este era el que faltaba registrar!)
        net.messageBuilder(ChargeKiPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ChargeKiPacket::new)
                .encoder(ChargeKiPacket::toBytes)
                .consumerNetworkThread(ChargeKiPacket::handle)
                .add();
    }

    public static <T> void sendToServer(T message) {
        INSTANCE.sendToServer(message);
    }

    public static <T> void sendToPlayer(T message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <T> void sendToAllPlayers(T message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
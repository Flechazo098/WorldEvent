package com.flechazo.worldevent.network;

import com.flechazo.worldevent.WorldEvent;
import com.flechazo.worldevent.network.packets.EventEndPacket;
import com.flechazo.worldevent.network.packets.EventStartPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(WorldEvent.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    public static void init() {
        INSTANCE.messageBuilder(EventStartPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(EventStartPacket::encode)
            .decoder(EventStartPacket::decode)
            .consumerMainThread(EventStartPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(EventEndPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(EventEndPacket::encode)
            .decoder(EventEndPacket::decode)
            .consumerMainThread(EventEndPacket::handle)
            .add();
        
        WorldEvent.LOGGER.info("Network handler initialized with {} packets", packetId);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
package com.flechazo.worldevent.network.packets;

import com.flechazo.worldevent.client.ClientEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 事件结束数据包
 */
public record EventEndPacket(UUID eventId) {

    public static void encode(EventEndPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.eventId);
    }

    public static EventEndPacket decode(FriendlyByteBuf buffer) {
        UUID eventId = buffer.readUUID();
        return new EventEndPacket(eventId);
    }

    public static void handle(EventEndPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientEventHandler.onEventEnd(packet.eventId);
            }
        });
        context.setPacketHandled(true);
    }

}
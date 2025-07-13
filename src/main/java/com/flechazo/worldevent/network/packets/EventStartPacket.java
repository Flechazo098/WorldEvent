package com.flechazo.worldevent.network.packets;

import com.flechazo.worldevent.client.ClientEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 事件开始数据包
 */
public record EventStartPacket(UUID eventId, ResourceLocation eventType, BlockPos centerPos) {

    public static void encode(EventStartPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.eventId);
        buffer.writeResourceLocation(packet.eventType);
        buffer.writeBlockPos(packet.centerPos);
    }

    public static EventStartPacket decode(FriendlyByteBuf buffer) {
        UUID eventId = buffer.readUUID();
        ResourceLocation eventType = buffer.readResourceLocation();
        BlockPos centerPos = buffer.readBlockPos();
        return new EventStartPacket(eventId, eventType, centerPos);
    }

    public static void handle(EventStartPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientEventHandler.onEventStart(packet.eventId, packet.eventType, packet.centerPos);
            }
        });
        context.setPacketHandled(true);
    }
}
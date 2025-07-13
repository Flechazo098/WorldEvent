package com.flechazo.worldevent.client;

import com.flechazo.worldevent.WorldEvent;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static final Map<UUID, ClientActiveEvent> ACTIVE_EVENTS = new ConcurrentHashMap<>();
    
    public static void onEventStart(UUID eventId, ResourceLocation eventType, BlockPos centerPos) {
        ClientActiveEvent clientEvent = new ClientActiveEvent(eventId, eventType, centerPos);
        ACTIVE_EVENTS.put(eventId, clientEvent);
        clientEvent.start();
        
        WorldEvent.LOGGER.info("Client received event start: {} at {}", eventType, centerPos);
    }
    
    public static void onEventEnd(UUID eventId) {
        ClientActiveEvent clientEvent = ACTIVE_EVENTS.remove(eventId);
        if (clientEvent != null) {
            clientEvent.end();
            WorldEvent.LOGGER.info("Client received event end: {}", eventId);
        }
    }
    
    public static Map<UUID, ClientActiveEvent> getActiveEvents() {
        return ACTIVE_EVENTS;
    }

    @Getter
    public static class ClientActiveEvent {
        private final UUID eventId;
        private final ResourceLocation eventType;
        private final BlockPos centerPos;
        private boolean active = false;
        
        public ClientActiveEvent(UUID eventId, ResourceLocation eventType, BlockPos centerPos) {
            this.eventId = eventId;
            this.eventType = eventType;
            this.centerPos = centerPos;
        }
        
        public void start() {
            active = true;
            // TODO 客户端视觉效果
        }
        
        public void end() {
            active = false;
            // TODO 清理客户端视觉效果
        }

    }
}
package com.flechazo.worldevent.core;

import com.flechazo.worldevent.Config;
import com.flechazo.worldevent.WorldEvent;
import com.flechazo.worldevent.api.trigger.TriggerContext;
import com.flechazo.worldevent.data.WorldEventDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = WorldEvent.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventTriggerManager {
    private static final Map<ServerLevel, Set<ActiveEvent>> ACTIVE_EVENTS = new ConcurrentHashMap<>();
    private static int tickCounter = 0;
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Config.enableWorldEvents) {
            return;
        }
        
        tickCounter++;

        if (tickCounter % Config.eventCheckInterval == 0) {
            checkEventTriggers(event.getServer().getAllLevels());
        }

        updateActiveEvents();
    }
    
    private static void checkEventTriggers(Iterable<ServerLevel> levels) {
        for (ServerLevel level : levels) {
            if (getActiveEventCount(level) >= Config.maxConcurrentEvents) {
                continue; // 跳过已达到最大并发事件数的维度
            }
            
            for (WorldEventDefinition eventDef : EventRegistry.getEnabledEvents()) {
                if (shouldTriggerEvent(eventDef, level)) {
                    triggerEvent(eventDef, level);
                }
            }
        }
    }
    
    private static boolean shouldTriggerEvent(WorldEventDefinition eventDef, ServerLevel level) {
        if (eventDef.impactScope().dimension().isPresent()) {
            if (!eventDef.impactScope().dimension().get().equals(level.dimension().location())) {
                return false;
            }
        }

        TriggerContext context = new TriggerContext(level, tickCounter);

        return eventDef.trigger().shouldTrigger(context);
    }
    
    private static void triggerEvent(WorldEventDefinition eventDef, ServerLevel level) {
        try {
            ActiveEvent activeEvent = new ActiveEvent(eventDef, level, tickCounter);
            
            ACTIVE_EVENTS.computeIfAbsent(level, k -> ConcurrentHashMap.newKeySet()).add(activeEvent);

            activeEvent.start();
            
            if (Config.enableDebugLogging) {
                WorldEvent.LOGGER.debug("Triggered event {} in dimension {}", 
                    eventDef.id(), level.dimension().location());
            }
        } catch (Exception e) {
            WorldEvent.LOGGER.error("Failed to trigger event {}: {}", eventDef.id(), e.getMessage(), e);
        }
    }
    
    private static void updateActiveEvents() {
        ACTIVE_EVENTS.forEach((level, events) -> {
            Iterator<ActiveEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
                ActiveEvent event = iterator.next();
                if (event.isExpired(tickCounter)) {
                    event.end();
                    iterator.remove();
                    
                    if (Config.enableDebugLogging) {
                        WorldEvent.LOGGER.debug("Event {} expired in dimension {}", 
                            event.getDefinition().id(), level.dimension().location());
                    }
                } else {
                    event.update(tickCounter);
                }
            }
        });
    }
    
    public static int getActiveEventCount(ServerLevel level) {
        return ACTIVE_EVENTS.getOrDefault(level, Collections.emptySet()).size();
    }
    
    public static Set<ActiveEvent> getActiveEvents(ServerLevel level) {
        return new HashSet<>(ACTIVE_EVENTS.getOrDefault(level, Collections.emptySet()));
    }
    
    public static void clearActiveEvents() {
        ACTIVE_EVENTS.clear();
    }
}
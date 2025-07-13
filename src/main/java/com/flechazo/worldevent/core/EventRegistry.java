package com.flechazo.worldevent.core;

import com.flechazo.worldevent.WorldEvent;
import com.flechazo.worldevent.data.WorldEventDefinition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = WorldEvent.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventRegistry {
    public static final ResourceKey<Registry<WorldEventDefinition>> WORLD_EVENTS_REGISTRY_KEY = 
        ResourceKey.createRegistryKey(new ResourceLocation(WorldEvent.MOD_ID, "world_events"));
    
    private static final Map<ResourceLocation, WorldEventDefinition> CACHED_EVENTS = new HashMap<>();
    private static Registry<WorldEventDefinition> registry;
    
    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(WORLD_EVENTS_REGISTRY_KEY, WorldEventDefinition.CODEC);
        WorldEvent.LOGGER.info("Registered world events datapack registry");
    }
    
    public static void init() {
        WorldEvent.LOGGER.info("EventRegistry initialized");
    }
    
    public static void setRegistry(Registry<WorldEventDefinition> newRegistry) {
        registry = newRegistry;
        refreshCache();
    }
    
    private static void refreshCache() {
        CACHED_EVENTS.clear();
        if (registry != null) {
            for (var entry : registry.entrySet()) {
                CACHED_EVENTS.put(entry.getKey().location(), entry.getValue());
            }
            WorldEvent.LOGGER.info("Cached {} world events", CACHED_EVENTS.size());
        }
    }
    
    public static Optional<WorldEventDefinition> getEvent(ResourceLocation id) {
        return Optional.ofNullable(CACHED_EVENTS.get(id));
    }
    
    public static Collection<WorldEventDefinition> getAllEvents() {
        return CACHED_EVENTS.values();
    }
    
    public static Collection<WorldEventDefinition> getEnabledEvents() {
        return CACHED_EVENTS.values().stream()
            .filter(WorldEventDefinition::enabled)
            .toList();
    }
    
    public static boolean hasEvent(ResourceLocation id) {
        return CACHED_EVENTS.containsKey(id);
    }
    
    public static int getEventCount() {
        return CACHED_EVENTS.size();
    }
}
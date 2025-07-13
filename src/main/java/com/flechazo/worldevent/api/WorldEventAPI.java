package com.flechazo.worldevent.api;

import com.flechazo.worldevent.WorldEvent;
import com.flechazo.worldevent.api.effect.IEventEffect;
import com.flechazo.worldevent.api.trigger.IEventTrigger;
import com.flechazo.worldevent.core.EventRegistry;
import com.flechazo.worldevent.data.WorldEventDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldEventAPI {
    private static final Map<ResourceLocation, IEventTrigger.Factory> TRIGGER_FACTORIES = new HashMap<>();
    private static final Map<ResourceLocation, IEventEffect.Factory> EFFECT_FACTORIES = new HashMap<>();
    
    public static void init() {
        WorldEvent.LOGGER.info("WorldEvent API initialized");
    }
    
    /**
     * 注册自定义触发器类型
     */
    public static void registerTriggerType(ResourceLocation id, IEventTrigger.Factory factory) {
        TRIGGER_FACTORIES.put(id, factory);
        WorldEvent.LOGGER.info("Registered trigger type: {}", id);
    }
    
    /**
     * 注册自定义效果类型
     */
    public static void registerEffectType(ResourceLocation id, IEventEffect.Factory factory) {
        EFFECT_FACTORIES.put(id, factory);
        WorldEvent.LOGGER.info("Registered effect type: {}", id);
    }
    
    /**
     * 获取触发器工厂
     */
    public static Optional<IEventTrigger.Factory> getTriggerFactory(ResourceLocation id) {
        return Optional.ofNullable(TRIGGER_FACTORIES.get(id));
    }
    
    /**
     * 获取效果工厂
     */
    public static Optional<IEventEffect.Factory> getEffectFactory(ResourceLocation id) {
        return Optional.ofNullable(EFFECT_FACTORIES.get(id));
    }
    
    /**
     * 获取所有已注册的事件定义
     */
    public static Collection<WorldEventDefinition> getAllEvents() {
        return EventRegistry.getAllEvents();
    }
    
    /**
     * 根据 ID 获取事件定义
     */
    public static Optional<WorldEventDefinition> getEvent(ResourceLocation id) {
        return EventRegistry.getEvent(id);
    }
    
    /**
     * 检查事件是否存在
     */
    public static boolean hasEvent(ResourceLocation id) {
        return EventRegistry.hasEvent(id);
    }
    
    /**
     * 获取已注册的触发器类型
     */
    public static Collection<ResourceLocation> getRegisteredTriggerTypes() {
        return TRIGGER_FACTORIES.keySet();
    }
    
    /**
     * 获取已注册的效果类型
     */
    public static Collection<ResourceLocation> getRegisteredEffectTypes() {
        return EFFECT_FACTORIES.keySet();
    }
}
package com.flechazo.worldevent.api.trigger;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * 事件触发器接口
 */
public interface IEventTrigger {
    /**
     * 检查是否应该触发事件
     */
    boolean shouldTrigger(TriggerContext context);
    
    /**
     * 获取触发器类型 ID
     */
    ResourceLocation getType();
    
    /**
     * 触发器工厂接口
     */
    interface Factory {
        IEventTrigger create();
        Codec<? extends IEventTrigger> getCodec();
    }
    
    // 分派编解码器，用于序列化不同类型的触发器
    Codec<IEventTrigger> CODEC = ResourceLocation.CODEC.dispatch(
        IEventTrigger::getType,
        type -> com.flechazo.worldevent.api.WorldEventAPI.getTriggerFactory(type)
            .map(Factory::getCodec)
            .orElseThrow(() -> new IllegalArgumentException("Unknown trigger type: " + type))
    );
}
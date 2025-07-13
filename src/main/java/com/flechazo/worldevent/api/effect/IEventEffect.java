package com.flechazo.worldevent.api.effect;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * 事件效果接口
 */
public interface IEventEffect {
    /**
     * 执行效果
     */
    void execute(EffectContext context);
    
    /**
     * 获取效果类型 ID
     */
    ResourceLocation getType();
    
    /**
     * 效果工厂接口
     */
    interface Factory {
        IEventEffect create();
        Codec<? extends IEventEffect> getCodec();
    }
    
    // 分派编解码器，用于序列化不同类型的效果
    Codec<IEventEffect> CODEC = ResourceLocation.CODEC.dispatch(
        IEventEffect::getType,
        type -> com.flechazo.worldevent.api.WorldEventAPI.getEffectFactory(type)
            .map(Factory::getCodec)
            .orElseThrow(() -> new IllegalArgumentException("Unknown effect type: " + type))
    );
}
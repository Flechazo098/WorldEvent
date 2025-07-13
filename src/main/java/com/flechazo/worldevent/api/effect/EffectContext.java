package com.flechazo.worldevent.api.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

/**
 * 效果执行上下文
 */
public record EffectContext(ServerLevel level, BlockPos centerPos, UUID eventId, boolean isStarting) {

    public boolean isEnding() {
        return !isStarting;
    }
}
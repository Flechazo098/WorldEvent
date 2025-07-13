package com.flechazo.worldevent.api.trigger;

import net.minecraft.server.level.ServerLevel;

/**
 * 触发器上下文，包含触发检查所需的信息
 */
public record TriggerContext(ServerLevel level, int currentTick) {

    public boolean isDay() {
        return level.isDay();
    }

    public boolean isNight() {
        return !level.isDay();
    }

    public boolean isRaining() {
        return level.isRaining();
    }

    public boolean isThundering() {
        return level.isThundering();
    }

    public long getDayTime() {
        return level.getDayTime();
    }

    public int getPlayerCount() {
        return level.players().size();
    }
}
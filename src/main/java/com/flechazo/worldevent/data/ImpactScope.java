package com.flechazo.worldevent.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * 事件影响范围定义
 */
public record ImpactScope(Optional<ResourceLocation> dimension, int radiusChunks, int durationTicks,
                          boolean persistsAcrossRestarts) {
    public static final Codec<ImpactScope> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("dimension").forGetter(scope -> scope.dimension),
                    Codec.INT.fieldOf("radius_chunks").forGetter(scope -> scope.radiusChunks),
                    Codec.INT.fieldOf("duration_ticks").forGetter(scope -> scope.durationTicks),
                    Codec.BOOL.optionalFieldOf("persists_across_restarts", false).forGetter(scope -> scope.persistsAcrossRestarts)
            ).apply(instance, ImpactScope::new)
    );

}
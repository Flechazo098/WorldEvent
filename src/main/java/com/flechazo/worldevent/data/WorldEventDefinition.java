package com.flechazo.worldevent.data;

import com.flechazo.worldevent.api.trigger.IEventTrigger;
import com.flechazo.worldevent.api.effect.IEventEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record WorldEventDefinition(ResourceLocation id, String displayName, String description, IEventTrigger trigger,
                                   List<IEventEffect> effects, ImpactScope impactScope,
                                   Optional<EventMessages> messages, int priority, boolean enabled) {
    public static final Codec<WorldEventDefinition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(def -> def.id),
                    Codec.STRING.fieldOf("display_name").forGetter(def -> def.displayName),
                    Codec.STRING.optionalFieldOf("description", "").forGetter(def -> def.description),
                    IEventTrigger.CODEC.fieldOf("trigger").forGetter(def -> def.trigger),
                    IEventEffect.CODEC.listOf().fieldOf("effects").forGetter(def -> def.effects),
                    ImpactScope.CODEC.fieldOf("impact_scope").forGetter(def -> def.impactScope),
                    EventMessages.CODEC.optionalFieldOf("messages").forGetter(def -> def.messages),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(def -> def.priority),
                    Codec.BOOL.optionalFieldOf("enabled", true).forGetter(def -> def.enabled)
            ).apply(instance, WorldEventDefinition::new)
    );

    public Component getDisplayComponent() {
        return Component.literal(displayName);
    }

    public Component getDescriptionComponent() {
        return Component.literal(description);
    }
}
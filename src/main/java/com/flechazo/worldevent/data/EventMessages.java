package com.flechazo.worldevent.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.Optional;

/**
 * 事件消息配置
 */
public record EventMessages(Optional<String> startMessage, Optional<String> endMessage, String color) {
    public static final Codec<EventMessages> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("start_message").forGetter(msg -> msg.startMessage),
                    Codec.STRING.optionalFieldOf("end_message").forGetter(msg -> msg.endMessage),
                    Codec.STRING.optionalFieldOf("color", "#FFFFFF").forGetter(msg -> msg.color)
            ).apply(instance, EventMessages::new)
    );

    public Optional<Component> getStartComponent() {
        return startMessage.map(msg -> Component.literal(msg).setStyle(getStyle()));
    }

    public Optional<Component> getEndComponent() {
        return endMessage.map(msg -> Component.literal(msg).setStyle(getStyle()));
    }

    private Style getStyle() {
        try {
            int colorValue = Integer.parseInt(color.replace("#", ""), 16);
            return Style.EMPTY.withColor(colorValue);
        } catch (NumberFormatException e) {
            return Style.EMPTY.withColor(ChatFormatting.WHITE);
        }
    }
}
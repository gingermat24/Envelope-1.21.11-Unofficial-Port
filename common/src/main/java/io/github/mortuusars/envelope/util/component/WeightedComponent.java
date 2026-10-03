package io.github.mortuusars.envelope.util.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record WeightedComponent(Component component) {
    public static final Codec<WeightedComponent> CODEC =
          ComponentSerialization.CODEC.xmap(WeightedComponent::new, WeightedComponent::component);

    public Component getComponent() {
        return component;
    }
}
package io.github.mortuusars.envelope.util.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;

public record ComponentProvider(WeightedList<WeightedComponent> list) {
    public static final Codec<ComponentProvider> CODEC = WeightedList.codec(WeightedComponent.CODEC)
          .xmap(ComponentProvider::new, ComponentProvider::list);

    public static final ComponentProvider EMPTY = new ComponentProvider(WeightedList.of());

    public static ComponentProvider of(Component component) {
        return new ComponentProvider(WeightedList.of(new WeightedComponent(component)));
    }

    public Component get(RandomSource randomSource) {
        return list.getRandom(randomSource).map(WeightedComponent::getComponent).orElse(CommonComponents.EMPTY);
    }
}

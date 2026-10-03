package io.github.mortuusars.envelope.client.gui;

import com.google.common.base.Preconditions;
import io.github.mortuusars.envelope.Envelope;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Sprites {
    public static final WidgetSprites CONFIRM_BUTTON_SPRITES =
            threeStates(Envelope.resource("button/confirm"));
    public static final WidgetSprites CANCEL_BUTTON_SPRITES =
            threeStates(Envelope.resource("button/cancel_button"));

    public static WidgetSprites normalOnly(Identifier base) {
        return new WidgetSprites(base, base);
    }

    public static WidgetSprites normalAndHighlighted(Identifier base) {
        return new WidgetSprites(base, base,
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }

    public static WidgetSprites normalAndHighlighted(Identifier normal, Identifier highlighted) {
        return new WidgetSprites(normal, normal, highlighted);
    }

    public static WidgetSprites threeStates(Identifier base) {
        return new WidgetSprites(base,
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_disabled"),
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }

    public static <T> Map<T, WidgetSprites> createMap(List<T> values, Function<T, WidgetSprites> convertFunc) {
        Preconditions.checkArgument(!values.isEmpty(), "values list must not be empty.");
        Map<T, WidgetSprites> map = new HashMap<>();
        for (T value : values) {
            map.put(value, convertFunc.apply(value));
        }
        return map;
    }
}

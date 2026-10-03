package io.github.mortuusars.envelope.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import java.util.List;

public class CompositeTooltipComponent implements ClientTooltipComponent {
    private final List<ClientTooltipComponent> components;

    public CompositeTooltipComponent(List<ClientTooltipComponent> components) {
        this.components = components;
    }

    @Override
    public int getWidth(Font font) {
        int width = 0;
        for (ClientTooltipComponent component : components) {
            width = Math.max(component.getWidth(font), width);
        }
        return width;
    }

    @Override
    public int getHeight(Font font) {
        int height = 0;
        for (ClientTooltipComponent component : components) {
            height += component.getHeight(font);
        }
        return height;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        for (ClientTooltipComponent component : components) {
            int componentHeight = component.getHeight(font);
            component.renderImage(font, x, y, component.getWidth(font), componentHeight, guiGraphics);
            y += componentHeight;
        }
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {
        for (ClientTooltipComponent component : components) {
            component.renderText(guiGraphics, font, x, y);
            y += component.getHeight(font);
        }
    }
}

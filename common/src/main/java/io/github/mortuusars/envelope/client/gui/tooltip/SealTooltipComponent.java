package io.github.mortuusars.envelope.client.gui.tooltip;

import io.github.mortuusars.envelope.EnvelopeClient;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;

public class SealTooltipComponent implements ClientTooltipComponent {
    protected final Seal seal;

    public SealTooltipComponent(Seal seal) {
        this.seal = seal;
    }

    @Override
    public int getWidth(Font font) {
        return 35 + font.width(seal.signature());
    }

    @Override
    public int getHeight(Font font) {
        return seal.lock().isPresent() ? 33 : 32;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        EnvelopeClient.getSealRenderer().render(seal, guiGraphics, x, y);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {
        // Signature:
        x += 34;
        y += 11;
        int color = seal.material().value().impressionPalette().highlight().tint();
        int outlineColor = seal.material().value().impressionPalette().shadow().tint();
        text(guiGraphics, seal.signature(), font, x - 1, y, outlineColor);
        text(guiGraphics, seal.signature(), font, x - 1, y - 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x, y - 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x + 1, y - 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x + 1, y, outlineColor);
        text(guiGraphics, seal.signature(), font, x + 1, y + 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x, y + 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x - 1, y + 1, outlineColor);
        text(guiGraphics, seal.signature(), font, x, y, color);
    }

    private void text(GuiGraphics guiGraphics, Component text, Font font, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, false);
    }
}

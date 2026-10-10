package io.github.mortuusars.envelope.client.gui.tooltip;

import io.github.mortuusars.envelope.EnvelopeClient;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SealTooltipComponent implements ClientTooltipComponent {
    protected final Seal seal;

    public SealTooltipComponent(Seal seal) {
        this.seal = seal;
    }

    @Override
    public int getWidth(Font font) {
        return 37 + font.width(seal.signature());
    }

    @Override
    public int getHeight(Font font) {
        return seal.lock().isPresent() ? 35 : 34;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        EnvelopeClient.getSealRenderer().render(seal, guiGraphics, x, y);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {
        // Signature:
        x += 36;
        y += 12;
        int color = lerpColor(0.25F, seal.material().value().impressionPalette().highlight().tint(), 0xFFFFFFFF);
        int outlineColor = lerpColor(0.25F, seal.material().value().impressionPalette().side().tint(), 0xFF7F7F7F);
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

    private static int lerpColor(float delta, int from, int to) {
        int alpha = Math.round(Mth.lerp(delta, from >>> 24 & 0xFF, to >>> 24 & 0xFF));
        int red = Math.round(Mth.lerp(delta, from >>> 16 & 0xFF, to >>> 16 & 0xFF));
        int green = Math.round(Mth.lerp(delta, from >>> 8 & 0xFF, to >>> 8 & 0xFF));
        int blue = Math.round(Mth.lerp(delta, from & 0xFF, to & 0xFF));
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}

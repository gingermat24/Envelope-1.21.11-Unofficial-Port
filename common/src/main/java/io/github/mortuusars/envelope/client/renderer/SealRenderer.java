package io.github.mortuusars.envelope.client.renderer;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.seal.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SealRenderer {
    public static final Identifier IRON_DIE_TEXTURE = Envelope.resource("textures/seal/die/iron.png");

    public void render(Seal seal, GuiGraphics guiGraphics, int x, int y) {
        SealMaterial material = seal.material().value();
        ShadingPalette colors = material.impressionPalette();

        Identifier materialTexture = material.texture();
        Identifier impressionTexture = seal.impression().value().texture();

        // Background
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, materialTexture, x, y, 0, 0, 30, 30, 30, 30);

        // Side
        int sideTint = colors.side().tint(0xFFFFFFFF);
        blitTinted(guiGraphics, impressionTexture, x + 1, y + 2, sideTint);
        blitTinted(guiGraphics, impressionTexture, x, y + 2, sideTint);
        blitTinted(guiGraphics, impressionTexture, x - 1, y + 2, sideTint);
        blitTinted(guiGraphics, impressionTexture, x + 1, y + 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x, y + 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x - 1, y + 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x + 1, y, sideTint);
        blitTinted(guiGraphics, impressionTexture, x, y, sideTint);
        blitTinted(guiGraphics, impressionTexture, x - 1, y, sideTint);
        blitTinted(guiGraphics, impressionTexture, x + 1, y - 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x, y - 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x - 1, y - 1, sideTint);
        blitTinted(guiGraphics, impressionTexture, x, y - 2, sideTint);

        // Shadow
        blitTinted(guiGraphics, impressionTexture, x, y + 1, colors.shadow().tint(0xFFFFFFFF));

        // Highlight
        blitTinted(guiGraphics, impressionTexture, x, y - 1, colors.highlight().tint(0xFFFFFFFF));

        // Base
        blitTinted(guiGraphics, impressionTexture, x, y, colors.base().tint(0xFFFFFFFF));
    }

    public void renderDie(SealSymbol impression, ShadingPalette colors, GuiGraphics guiGraphics, int x, int y) {
        Identifier impressionTexture = impression.texture();

        // Background
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, IRON_DIE_TEXTURE, x, y, 0, 0, 30, 30, 30, 30);

        // textureWidth parameter is negative to flip the impression texture on the X axis

        // Side
        int sideTint = colors.side().tint(0xFFFFFFFF);
        blitTintedFlipped(guiGraphics, impressionTexture, x + 1, y + 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x, y + 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x - 1, y + 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x + 1, y, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x, y, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x - 1, y, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x + 1, y - 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x, y - 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x - 1, y - 1, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x + 1, y - 2, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x, y - 2, sideTint);
        blitTintedFlipped(guiGraphics, impressionTexture, x - 1, y - 2, sideTint);

        // Highlight
        blitTintedFlipped(guiGraphics, impressionTexture, x, y + 1, colors.highlight().tint(0xFFFFFFFF));

        // Shadow
        blitTintedFlipped(guiGraphics, impressionTexture, x, y - 1, colors.shadow().tint(0xFFFFFFFF));

        // Base
        blitTintedFlipped(guiGraphics, impressionTexture, x, y, colors.base().tint(0xFFFFFFFF));
    }

    private static void blitTinted(GuiGraphics guiGraphics, Identifier texture, int x, int y, int color) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, 30, 30, 30, 30, color);
    }

    private static void blitTintedFlipped(GuiGraphics guiGraphics, Identifier texture, int x, int y, int color) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 30, 0, 30, 30, -30, 30, 30, 30, color);
    }

    // Have you seen someone rendering textures this way? Now you have.
    // Patent Pending ™
}

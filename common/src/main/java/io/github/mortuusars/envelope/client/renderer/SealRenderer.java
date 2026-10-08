package io.github.mortuusars.envelope.client.renderer;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.util.TintColor;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import io.github.mortuusars.envelope.world.item.component.seal.SealSymbol;
import io.github.mortuusars.envelope.world.item.component.seal.ShadingPalette;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class SealRenderer {
    public static final Identifier IRON_DIE_TEXTURE = Envelope.resource("textures/gui/sprites/seal/die/iron.png");
    private static final Identifier SCULK_SPRITE = Envelope.resource("seal/material/sculk");
    private static final Identifier SEAL_GLINT_SPRITE = Envelope.resource("seal/glint");
    private static final Identifier LOCK_SPRITE = Envelope.resource("seal/lock");
    private static final Identifier LOCKED_OVERLAY_SPRITE = Envelope.resource("seal/locked_overlay");
    private static final int SCULK_FRAME_SIZE = 30;
    private static final int SCULK_FRAME_COUNT = 4;
    private static final long SCULK_FRAME_DURATION_MILLIS = 1000L;

    public void render(Seal seal, GuiGraphics guiGraphics, int x, int y) {
        SealMaterial material = seal.material().value();
        ShadingPalette colors = material.impressionPalette();

        Identifier impressionTexture = seal.impression().value().spriteTexture();

        renderMaterial(material, guiGraphics, x, y);

        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y + 2, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y + 2, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y + 2, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y + 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y + 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y + 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y - 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y - 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y - 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y - 2, 30);

        blitImpression(guiGraphics, impressionTexture, colors.shadow(), x, y + 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.highlight(), x, y - 1, 30);
        blitImpression(guiGraphics, impressionTexture, colors.base(), x, y, 30);

        if (material.hasGlint()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEAL_GLINT_SPRITE, x, y, 32, 32);
        }

        if (seal.lock().isPresent()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOCKED_OVERLAY_SPRITE, x - 2, y - 2, 34, 34);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOCK_SPRITE, x - 2, y - 2, 34, 34);
        }
    }

    public void renderDie(SealSymbol impression, ShadingPalette colors, GuiGraphics guiGraphics, int x, int y) {
        renderDie(impression, IRON_DIE_TEXTURE, colors, guiGraphics, x, y);
    }

    public void renderDie(SealSymbol impression, SealMaterial material, GuiGraphics guiGraphics, int x, int y) {
        renderMaterial(material, guiGraphics, x, y);
        renderDieImpression(impression, material.impressionPalette(), guiGraphics, x, y);
    }

    private void renderDie(SealSymbol impression, Identifier backgroundTexture, ShadingPalette colors,
                           GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, x, y, 0, 0, 30, 30, 30, 30);
        renderDieImpression(impression, colors, guiGraphics, x, y);
    }

    private void renderDieImpression(SealSymbol impression, ShadingPalette colors,
                                    GuiGraphics guiGraphics, int x, int y) {
        Identifier impressionTexture = impression.spriteTexture();
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y + 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y + 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y + 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y - 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y - 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y - 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x + 1, y - 2, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x, y - 2, -30);
        blitImpression(guiGraphics, impressionTexture, colors.side(), x - 1, y - 2, -30);
        blitImpression(guiGraphics, impressionTexture, colors.highlight(), x, y + 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.shadow(), x, y - 1, -30);
        blitImpression(guiGraphics, impressionTexture, colors.base(), x, y, -30);
    }

    private void renderMaterial(SealMaterial material, GuiGraphics guiGraphics, int x, int y) {
        if (material.spriteId().equals(SCULK_SPRITE)) {
            int frame = (int) ((Util.getMillis() / SCULK_FRAME_DURATION_MILLIS) % SCULK_FRAME_COUNT);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, material.spriteTexture(),
                  x, y, 0, frame * SCULK_FRAME_SIZE,
                  SCULK_FRAME_SIZE, SCULK_FRAME_SIZE,
                  SCULK_FRAME_SIZE, SCULK_FRAME_SIZE * SCULK_FRAME_COUNT);
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, material.spriteId(), x, y, 30, 30);
        }
    }

    private void blitImpression(GuiGraphics guiGraphics, Identifier source, TintColor tint, int x, int y, int textureWidth) {
        Identifier tintedTexture = TintedTextureCache.get(source, tint);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, tintedTexture, x, y, 0, 0, 30, 30, textureWidth, 30, 0xFFFFFFFF);
    }
}
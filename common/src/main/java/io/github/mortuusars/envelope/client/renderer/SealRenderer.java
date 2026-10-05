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

public class SealRenderer {
    public static final Identifier IRON_DIE_TEXTURE = Envelope.resource("textures/seal/die/iron.png");
    private static final Identifier NEUTRAL_WAX_TEXTURE = Envelope.resource("seal/material/neutral_wax");

    public void render(Seal seal, GuiGraphics guiGraphics, int x, int y) {
        SealMaterial material = seal.material().value();
        ShadingPalette colors = material.impressionPalette();

        Identifier materialTexture = getMaterialTexture(material);
        Identifier impressionTexture = seal.impression().value().texture();

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, materialTexture, x, y, 0, 0, 30, 30, 30, 30);

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
    }

    public void renderDie(SealSymbol impression, ShadingPalette colors, GuiGraphics guiGraphics, int x, int y) {
        renderDie(impression, IRON_DIE_TEXTURE, colors, guiGraphics, x, y);
    }

    public void renderDie(SealSymbol impression, SealMaterial material, GuiGraphics guiGraphics, int x, int y) {
        renderDie(impression, getMaterialTexture(material), material.impressionPalette(), guiGraphics, x, y);
    }

    private void renderDie(SealSymbol impression, Identifier backgroundTexture, ShadingPalette colors,
                           GuiGraphics guiGraphics, int x, int y) {
        Identifier impressionTexture = impression.texture();

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, x, y, 0, 0, 30, 30, 30, 30);

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

    private Identifier getMaterialTexture(SealMaterial material) {
        Identifier texture = material.texture();
        if (material.textureId().equals(NEUTRAL_WAX_TEXTURE)) {
            return TintedTextureCache.get(texture, TintColor.of(material.modelTintColor()));
        }
        return texture;
    }

    private void blitImpression(GuiGraphics guiGraphics, Identifier source, TintColor tint, int x, int y, int textureWidth) {
        Identifier tintedTexture = TintedTextureCache.get(source, tint);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, tintedTexture, x, y, 0, 0, 30, 30, textureWidth, 30, 0xFFFFFFFF);
    }
}
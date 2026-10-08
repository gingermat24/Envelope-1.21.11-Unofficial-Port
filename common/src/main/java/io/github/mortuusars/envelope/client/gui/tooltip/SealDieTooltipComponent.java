package io.github.mortuusars.envelope.client.gui.tooltip;

import io.github.mortuusars.envelope.EnvelopeClient;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import io.github.mortuusars.envelope.world.item.component.seal.SealSymbol;
import io.github.mortuusars.envelope.world.item.component.seal.ShadingPalette;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public class SealDieTooltipComponent implements ClientTooltipComponent {
    protected final Holder<SealSymbol> symbol;
    protected final Optional<Holder<SealMaterial>> material;
    protected final boolean soulbound;

    public SealDieTooltipComponent(Holder<SealSymbol> symbol, Optional<Holder<SealMaterial>> material) {
        this(symbol, material, false);
    }

    public SealDieTooltipComponent(Holder<SealSymbol> symbol, Optional<Holder<SealMaterial>> material,
                                   boolean soulbound) {
        this.symbol = symbol;
        this.material = material;
        this.soulbound = soulbound;
    }

    public SealDieTooltipComponent(Optional<Holder<SealSymbol>> symbolHolder, Optional<Holder<SealMaterial>> material) {
        this(getSymbol(symbolHolder), material);
    }

    public SealDieTooltipComponent(Optional<Holder<SealSymbol>> symbolHolder, Optional<Holder<SealMaterial>> material,
                                   boolean soulbound) {
        this(getSymbol(symbolHolder), material, soulbound);
    }

    private static Holder<SealSymbol> getSymbol(Optional<Holder<SealSymbol>> symbolHolder) {
        return symbolHolder
              .orElseGet(() -> {
                  ResourceKey<SealSymbol> key = SealSymbol.firstCharOrDefault(Minecrft.player());
                  return SealSymbol.getOrThrow(Minecrft.registryAccess(), key);
              });
    }

    @Override
    public int getWidth(Font font) {
        return 31;
    }

    @Override
    public int getHeight(Font font) {
        return 31;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        if (material.isPresent()) {
            EnvelopeClient.getSealRenderer().renderDie(symbol.value(), material.get().value(), guiGraphics, x - 1, y - 1);
        } else if (soulbound) {
            SealMaterial sculk = SealMaterial.getOrThrow(Minecrft.registryAccess(), SealMaterial.SCULK).value();
            EnvelopeClient.getSealRenderer().renderDie(symbol.value(), sculk, guiGraphics, x - 1, y - 1);
        } else {
            EnvelopeClient.getSealRenderer().renderDie(symbol.value(), ShadingPalette.IRON_DIE, guiGraphics, x - 1, y - 1);
        }
    }
}

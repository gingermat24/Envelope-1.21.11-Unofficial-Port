package io.github.mortuusars.envelope.world.inventory.tooltip;

import io.github.mortuusars.envelope.world.item.component.seal.SealSymbol;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Optional;

public record SealDieTooltipComponent(Optional<Holder<SealSymbol>> impression,
                                      Optional<Holder<SealMaterial>> material,
                                      boolean soulbound) implements TooltipComponent {
    public SealDieTooltipComponent(Optional<Holder<SealSymbol>> impression,
                                   Optional<Holder<SealMaterial>> material) {
        this(impression, material, false);
    }
}

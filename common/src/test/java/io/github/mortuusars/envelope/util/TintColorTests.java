package io.github.mortuusars.envelope.util;

import io.github.mortuusars.envelope.world.item.component.seal.ShadingPalette;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TintColorTests {
    @Test
    void neutralGraySourceReproducesConfiguredPaletteColors() {
        ShadingPalette palette = new ShadingPalette(
                0xFFA73A34,
                0xFFF18E78,
                0xFF660C0A,
                0xFF8A2622);

        assertEquals(0xFFA73A34, palette.getBaseArgb());
        assertEquals(0xFFF18E78, palette.getHighlightArgb());
        assertEquals(0xFF660C0A, palette.getShadowArgb());
        assertEquals(0xFF8A2622, palette.getSideArgb());
    }

    @Test
    void tintAppliesMultipliersPerSourcePixelWithoutClampingBrightChannelsEarly() {
        TintColor tint = TintColor.of(0xFFA73A34);

        assertEquals(0xFFA73A34, tint.tintPixel(0xFF7F7F7F));
        assertEquals(0xFFFF7468, tint.tintPixel(0xFFFFFFFF));
    }

    @Test
    void tintUsesOriginalMultiplierTruncation() {
        TintColor tint = TintColor.of(0xFFA73A34);

        assertEquals(0xFF842E29, tint.tint(0xFF656565));
    }
}

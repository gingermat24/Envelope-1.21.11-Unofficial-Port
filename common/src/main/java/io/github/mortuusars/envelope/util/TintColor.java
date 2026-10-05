package io.github.mortuusars.envelope.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Encodes palette colors as multipliers centered on mid-gray, matching the original seal-impression shading.
 * The GUI renderer applies these multipliers to source pixels before uploading a cached tinted texture.
 */
public record TintColor(float r, float g, float b, float a) {
    public static final Codec<TintColor> CODEC = EnvelopeCodecs.HEX_COLOR.xmap(TintColor::of, TintColor::tint);
    public static final StreamCodec<ByteBuf, TintColor> STREAM_CODEC = ByteBufCodecs.INT.map(TintColor::of, TintColor::tint);

    public static TintColor of(int argb) {
        float a = (float) (ARGB.alpha(argb) - 127) / 127 + 1;
        float r = (float) (ARGB.red(argb) - 127) / 127 + 1;
        float g = (float) (ARGB.green(argb) - 127) / 127 + 1;
        float b = (float) (ARGB.blue(argb) - 127) / 127 + 1;
        return new TintColor(r, g, b, a);
    }

    public int tint(int argb) {
        int a = Math.round(Mth.clamp(ARGB.alpha(argb) * this.a, 0, 255));
        int r = Math.round(Mth.clamp(ARGB.red(argb) * this.r, 0, 255));
        int g = Math.round(Mth.clamp(ARGB.green(argb) * this.g, 0, 255));
        int b = Math.round(Mth.clamp(ARGB.blue(argb) * this.b, 0, 255));
        return ARGB.color(a, r, g, b);
    }

    public int tint() {
        return tint(0xFF7F7F7F);
    }
}

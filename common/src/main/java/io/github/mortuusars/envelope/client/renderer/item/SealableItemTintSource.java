package io.github.mortuusars.envelope.client.renderer.item;

import com.mojang.serialization.MapCodec;
import io.github.mortuusars.envelope.world.item.Sealable;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SealableItemTintSource() implements ItemTintSource {
    public static final MapCodec<SealableItemTintSource> CODEC = MapCodec.unit(new SealableItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        @Nullable Seal seal = itemStack.get(io.github.mortuusars.envelope.Envelope.DataComponents.SEAL);
        if (seal == null || clientLevel == null) {
            return Sealable.getSealOverlayColor(itemStack, 1);
        }

        int materialColor = seal.material().value().modelTintColor();
        double time = clientLevel.getGameTime()
              + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (seal.lock().isPresent() && seal.lock().get().isLocked(clientLevel)) {
            double beat = Math.pow((Math.sin(time / 20.0 * Math.PI * 2.0) + 1.0) * 0.5, 10.0);
            return lerpColor((float) beat,
                  materialColor, 0xFF18A2B6);
        }

        if (seal.material().is(SealMaterial.GOLD)) {
            double pulse = (Math.sin(time / 10.0) + 1.0) * 0.5;
            return lerpColor((float) pulse, materialColor, 0xFFFFD16E);
        }

        return materialColor;
    }

    private static int lerpColor(float delta, int from, int to) {
        int alpha = Math.round(Mth.lerp(delta, from >>> 24 & 0xFF, to >>> 24 & 0xFF));
        int red = Math.round(Mth.lerp(delta, from >>> 16 & 0xFF, to >>> 16 & 0xFF));
        int green = Math.round(Mth.lerp(delta, from >>> 8 & 0xFF, to >>> 8 & 0xFF));
        int blue = Math.round(Mth.lerp(delta, from & 0xFF, to & 0xFF));
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    @Override
    public MapCodec<SealableItemTintSource> type() {
        return CODEC;
    }
}

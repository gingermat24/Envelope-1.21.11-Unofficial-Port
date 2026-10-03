package io.github.mortuusars.envelope.client.renderer.item;

import com.mojang.serialization.MapCodec;
import io.github.mortuusars.envelope.world.item.Sealable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SealableItemTintSource() implements ItemTintSource {
    public static final MapCodec<SealableItemTintSource> CODEC = MapCodec.unit(new SealableItemTintSource());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        return Sealable.getSealOverlayColor(itemStack, 1);
    }

    @Override
    public MapCodec<SealableItemTintSource> type() {
        return CODEC;
    }
}

package io.github.mortuusars.envelope.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.PaybackRequest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record PaybackTagDurationSelectProperty() implements SelectItemModelProperty<String> {
    public static final Type<PaybackTagDurationSelectProperty, String> TYPE = Type.create(
          MapCodec.unit(new PaybackTagDurationSelectProperty()), Codec.STRING);

    @Override
    public @Nullable String get(ItemStack itemStack, @Nullable ClientLevel clientLevel,
                                @Nullable LivingEntity livingEntity, int seed, ItemDisplayContext displayContext) {
        @Nullable PaybackRequest request = itemStack.get(Envelope.DataComponents.PAYBACK_TAG_CONTENTS);
        return request != null ? request.duration().getSerializedName() : null;
    }

    @Override
    public @NotNull Codec<String> valueCodec() {
        return Codec.STRING;
    }

    @Override
    public Type<PaybackTagDurationSelectProperty, String> type() {
        return TYPE;
    }
}

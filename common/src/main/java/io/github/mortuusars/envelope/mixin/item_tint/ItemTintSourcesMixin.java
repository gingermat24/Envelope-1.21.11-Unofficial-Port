package io.github.mortuusars.envelope.mixin.item_tint;

import com.mojang.serialization.MapCodec;
import io.github.mortuusars.envelope.client.renderer.item.SealableItemTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemTintSources.class)
public abstract class ItemTintSourcesMixin {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemTintSource>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void envelope$registerSealTintSource(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath("envelope", "seal"), SealableItemTintSource.CODEC);
    }
}

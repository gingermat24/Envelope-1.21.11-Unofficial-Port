package io.github.mortuusars.envelope.mixin.item_tint;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.renderer.item.PaybackTagDurationSelectProperty;
import io.github.mortuusars.envelope.client.renderer.item.SealStampMaterialSelectProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectItemModelProperties.class)
public abstract class SelectItemModelPropertiesMixin {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, SelectItemModelProperty.Type<?, ?>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void envelope$registerPaybackTagDurationProperty(CallbackInfo ci) {
        ID_MAPPER.put(Envelope.resource("payback_tag_duration"), PaybackTagDurationSelectProperty.TYPE);
        ID_MAPPER.put(Envelope.resource("seal_stamp_material"), SealStampMaterialSelectProperty.TYPE);
    }
}

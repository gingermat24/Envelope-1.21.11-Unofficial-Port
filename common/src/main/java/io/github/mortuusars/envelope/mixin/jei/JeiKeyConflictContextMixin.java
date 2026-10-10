package io.github.mortuusars.envelope.mixin.jei;

import io.github.mortuusars.envelope.integration.jei.JeiCompatibleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "mezz.jei.gui.input.handlers.GlobalInputHandler", remap = false)
public class JeiKeyConflictContextMixin {
    /**
     * Fixes keys such as Ctrl+O used in formatting.
     */
    @Inject(method = "handleUserInput", at = @At("HEAD"), cancellable = true)
    private void onHandleUserInput(@Coerce Object screen, @Coerce Object guiProperties, @Coerce Object input, @Coerce Object keyBindings,
                                   CallbackInfoReturnable<Optional<?>> cir) {
        if (screen instanceof JeiCompatibleScreen resolverScreen && resolverScreen.shouldBlockJeiInput()) {
            cir.setReturnValue(Optional.empty());
        }
    }
}

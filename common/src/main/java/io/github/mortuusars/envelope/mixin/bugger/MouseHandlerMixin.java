package io.github.mortuusars.envelope.mixin.bugger;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.envelope.util.bugger.BuggerDebugScreen;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            cancellable = true)
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci,
                  @Local(ordinal = 4 /* Magic number that corresponds to yScroll variable*/) double yScroll) {
        if (yScroll != 0 && BuggerDebugScreen.onMouseScroll(yScroll)) {
            ci.cancel();
        }
    }

    @Inject(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
            ordinal = 0), cancellable = true)
    private void envelope$onButton(long windowPointer, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (BuggerDebugScreen.onMousePress(buttonInfo.button(), action, buttonInfo.modifiers())) {
            ci.cancel();
        }
    }
}
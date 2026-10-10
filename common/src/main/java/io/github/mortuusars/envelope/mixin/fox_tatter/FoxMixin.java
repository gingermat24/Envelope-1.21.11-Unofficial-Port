package io.github.mortuusars.envelope.mixin.fox_tatter;

import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.world.item.LetterItem;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Fox.class)
public class FoxMixin {
    @ModifyVariable(method = "spitOutItem", at = @At("HEAD"), argsOnly = true)
    private ItemStack onSpitOutItem(ItemStack stack) {
        if (Config.Server.FOX_LETTER_TATTERING.get()) {
            return LetterItem.tatterIfApplicable(stack);
        }
        return stack;
    }
}

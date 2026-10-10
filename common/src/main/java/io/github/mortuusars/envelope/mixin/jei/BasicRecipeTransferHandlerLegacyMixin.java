package io.github.mortuusars.envelope.mixin.jei;

import io.github.mortuusars.envelope.network.Packets;
import io.github.mortuusars.envelope.network.packet.serverbound.PackingMenuPresetAddressC2SP;
import io.github.mortuusars.envelope.world.inventory.PackingMenu;
import io.github.mortuusars.envelope.world.item.crafting.mail.MailRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "mezz.jei.library.transfer.BasicRecipeTransferHandler", remap = false)
public abstract class BasicRecipeTransferHandlerLegacyMixin {
    @Inject(method = "transferRecipe", at = @At(value = "INVOKE", target = "Lmezz/jei/common/network/IConnectionToServer;sendPacketToServer(Lmezz/jei/common/network/packets/PlayToServerPacket;)V"))
    private void onTransferRecipe(@Coerce Object container, Object recipe, @Coerce Object recipeSlotsView,
                                  @Coerce Object player, boolean maxTransfer, boolean doTransfer,
                                  CallbackInfoReturnable<?> cir) {
        @Nullable Recipe<?> actualRecipe = null;

        if (recipe instanceof Recipe<?> direct) {
            actualRecipe = direct;
        } else if (recipe instanceof RecipeHolder<?> holder) {
            actualRecipe = holder.value();
        }

        if (container instanceof PackingMenu packingMenu && actualRecipe instanceof MailRecipe mailRecipe) {
            packingMenu.presetAddress(mailRecipe.getAddress());
            Packets.sendToServer(new PackingMenuPresetAddressC2SP(Optional.ofNullable(mailRecipe.getAddress())));
        }
    }
}

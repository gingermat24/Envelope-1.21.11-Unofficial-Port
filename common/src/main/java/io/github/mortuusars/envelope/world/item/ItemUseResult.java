package io.github.mortuusars.envelope.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public final class ItemUseResult {
    private ItemUseResult() {
    }

    public static InteractionResult success(ItemStack stack) {
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    public static InteractionResult sidedSuccess(ItemStack stack, boolean clientSide) {
        InteractionResult.Success result = clientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        return result.heldItemTransformedTo(stack);
    }

    public static InteractionResult fail(ItemStack stack) {
        return InteractionResult.FAIL;
    }
}

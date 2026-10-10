package io.github.mortuusars.envelope.world.item;

import io.github.mortuusars.envelope.Envelope;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class SealedLetterItem extends Item implements Unsealable {
    private final Supplier<? extends Item> baseItem;

    public SealedLetterItem(Supplier<? extends Item> baseItem, Properties properties) {
        super(Unsealable.withUnsealingConsumable(properties));
        this.baseItem = baseItem;
    }

    @Override
    public ItemLike getUnsealedItem() {
        return baseItem.get();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(Envelope.DataComponents.SEAL));
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getUnsealingDuration(stack, entity);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!canUnseal(stack, player)) {
            player.playSound(SoundEvents.COMPARATOR_CLICK);
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return ItemUseResult.success(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return unseal(stack, level, entity);
    }

    @Override
    public ItemStack unseal(ItemStack stack, Level level, @org.jetbrains.annotations.Nullable LivingEntity entity) {
        if (stack.has(Envelope.DataComponents.LETTER_TATTERED)) {
            if (entity instanceof Player player && !canUnseal(stack, player)) {
                return stack;
            }

            ItemStack unsealedStack = stack.transmuteCopy(Envelope.Items.TATTERED_LETTER.get());
            unsealedStack.remove(Envelope.DataComponents.SEAL);
            unsealedStack.remove(Envelope.DataComponents.LETTER_TATTERED);
            onUnsealed(stack, unsealedStack, level, entity);
            return unsealedStack;
        }
        return Unsealable.super.unseal(stack, level, entity);
    }
}

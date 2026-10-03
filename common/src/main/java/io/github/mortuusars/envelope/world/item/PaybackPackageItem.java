package io.github.mortuusars.envelope.world.item;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.Platform;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.util.Colors;
import io.github.mortuusars.envelope.world.GameTime;
import io.github.mortuusars.envelope.world.inventory.PaybackPackageMenu;
import io.github.mortuusars.envelope.world.item.component.PaybackSubject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaybackPackageItem extends Item {
    public PaybackPackageItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(Envelope.DataComponents.PACKAGE_CONTENTS));
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display,
                                java.util.function.Consumer<Component> components, TooltipFlag flag) {
        appendPaybackHoverText(stack, context, display, components, flag);
    }

    public static void appendPaybackHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display,
                                              java.util.function.Consumer<Component> components, TooltipFlag flag) {
        appendPaybackSubjectHoverText(components, stack.get(Envelope.DataComponents.PAYBACK_SUBJECT));
    }

    public static void appendPaybackSubjectHoverText(java.util.function.Consumer<Component> components, @Nullable PaybackSubject subject) {
        if (subject == null || subject.mail().isEmpty()) {
            return;
        }

        long remainingTicks = subject.expiresAt() - Minecrft.level().getGameTime();
        if (remainingTicks < 1) {
            components.accept(Component.translatable("gui.envelope.payback.expired").withColor(Colors.TOOLTIP_RED));
        } else {
            MutableComponent time = net.minecraft.client.Minecraft.getInstance().hasShiftDown()
                  ? GameTime.format(remainingTicks, false)
                  : GameTime.formatLargest(remainingTicks, false);

            components.accept(Component.literal("⌛ ").append(time).withColor(Colors.TOOLTIP_RED));
        }
    }

    // --

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            SimpleMenuProvider menuProvider = new SimpleMenuProvider(
                  (id, inventory, pl) -> new PaybackPackageMenu(id, inventory, hand), stack.getHoverName());
            Platform.openMenu(serverPlayer, menuProvider, buffer -> buffer.writeEnum(hand));
        }

        player.level().playSound(player, player, Envelope.SoundEvents.PAPER_TEAR.get(), SoundSource.PLAYERS, 0.6f, 0.95f);
        return ItemUseResult.success(stack);
    }
}
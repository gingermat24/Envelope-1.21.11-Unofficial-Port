package io.github.mortuusars.envelope;

import io.github.mortuusars.envelope.client.gui.tooltip.*;
import io.github.mortuusars.envelope.client.renderer.SealRenderer;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.util.bugger.BuggerDebugScreen;
import io.github.mortuusars.envelope.util.bugger.BuggerEntityOverhead;
import io.github.mortuusars.envelope.util.bugger_data.EnvelopeBuggerPage;
import io.github.mortuusars.envelope.util.bugger_data.PigeonEntityDataDisplay;
import io.github.mortuusars.envelope.world.item.component.*;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryLog;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryRecord;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.item.tooltip.CompositeTooltip;
import io.github.mortuusars.envelope.world.item.tooltip.MailAddressTagTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;
import java.util.function.Consumer;

public class EnvelopeClient {
    private static final SealRenderer sealRenderer = new SealRenderer();

    public static void init() {
        BuggerDebugScreen.addPage(new EnvelopeBuggerPage());
        BuggerEntityOverhead.addData(new PigeonEntityDataDisplay());
    }

    public static SealRenderer getSealRenderer() {
        return sealRenderer;
    }

    // --

    public static class TooltipComponents {
        public static ClientTooltipComponent create(TooltipComponent component) {
            return switch (component) {
                case MailAddressTagTooltip mailAddress -> new MailAddressTagTooltipComponent(mailAddress.address());
                case PackageContents packageContents -> new PackageTooltipComponent(packageContents);
                case PaybackRequest paybackRequest -> new PaybackRequestTooltipComponent(paybackRequest);
                case Seal seal -> new SealTooltipComponent(seal);
                case io.github.mortuusars.envelope.world.inventory.tooltip.SealDieTooltipComponent die ->
                      new SealDieTooltipComponent(die.impression(), die.material());
                case CompositeTooltip composite -> new CompositeTooltipComponent(
                      composite.components().stream().map(ClientTooltipComponent::create).toList()
                );
                default -> null;
            };
        }

        public static Optional<TooltipComponent> modifyTooltipImage(ItemStack stack, Optional<TooltipComponent> original) {
            if (stack.is(Envelope.Tags.Items.MAILABLE)) {
                return CompositeTooltip.of(
                      original,
                      Optional.ofNullable(stack.get(Envelope.DataComponents.MAIL_RECIPIENT)).map(MailAddressTagTooltip::new),
                      Optional.ofNullable(stack.get(Envelope.DataComponents.MAIL_PAYBACK_REQUEST))
                );
            }

            return original;
        }

        public static void appendTooltipLines(ItemStack stack, Consumer<Component> consumer,
                                              Item.TooltipContext context, Player player, TooltipFlag tooltipFlag) {
            Mail.getSender(stack).ifPresent(sender -> {
                DeliveryLog deliveryLog = Mail.getLog(stack);
                if (net.minecraft.client.Minecraft.getInstance().hasShiftDown() && !deliveryLog.isEmpty()) {
                    consumer.accept(Component.translatable("gui.envelope.delivery_log"));
                    for (DeliveryRecord record : deliveryLog.records()) {
                        consumer.accept(record.getDisplayComponent());
                    }
                } else {
                    consumer.accept(Component.translatable("gui.envelope.mail.from").withStyle(ChatFormatting.GRAY)
                          .append(": ").withStyle(ChatFormatting.GRAY)
                          .append(sender.format().asNeutral().toComponent()));
                }
            });

            if (tooltipFlag.isAdvanced()) {
                Optional.ofNullable(Mail.getId(stack)).ifPresent(id -> {
                    consumer.accept(Component.literal("Mail Id: " + id).withStyle(ChatFormatting.DARK_GRAY));
                });
            }
        }
    }
}

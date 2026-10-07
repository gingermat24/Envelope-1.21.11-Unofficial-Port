package io.github.mortuusars.envelope.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.mortuusars.envelope.command.argument.AddressArgument;
import io.github.mortuusars.envelope.command.suggestion.AddressSuggestions;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.util.Colors;
import io.github.mortuusars.envelope.world.mail.delivery.Delivery;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.mail.address.Address;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.world.mail.address.type.BlockAddress;
import io.github.mortuusars.envelope.world.mail.address.type.PlayerAddress;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class EnvelopeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("envelope")
              .requires((stack) -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
              .then(mailCommands(context))
              .then(Commands.literal("mailbox")
                    .then(Commands.literal("list")
                          .executes(EnvelopeCommand::listAllMailboxes)
                          .then(Commands.literal("default")
                                .executes(EnvelopeCommand::listDefaultMailboxes)))
                    .then(Commands.literal("position")
                          .then(Commands.argument("address", AddressArgument.block())
                                .suggests(AddressSuggestions.block())
                                .executes(c -> mailboxPosition(c, AddressArgument.getBlock(c, "address"))))))
              .then(EnvelopeDebugCommand.commands()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> mailCommands(CommandBuildContext context) {
        var mailArgument = Commands.argument("mail", ItemArgument.item(context))
              .executes(c -> sendMail(c, ItemArgument.getItem(c, "mail"), Optional.empty(), Optional.empty()));
        mailArgument.then(Commands.literal("to")
              .then(Commands.argument("recipient", CompoundTagArgument.compoundTag())
                    .executes(c -> sendMailTo(c, Optional.empty()))));

        var senderArgument = Commands.argument("sender", CompoundTagArgument.compoundTag())
              .executes(EnvelopeCommand::sendMailFrom);
        senderArgument.then(Commands.literal("to")
              .then(Commands.argument("recipient", CompoundTagArgument.compoundTag())
                    .executes(c -> sendMailTo(c,
                          Optional.of(parseAddress(c, CompoundTagArgument.getCompoundTag(c, "sender")))))));
        mailArgument.then(Commands.literal("from").then(senderArgument));

        var broadcastMailArgument = Commands.argument("mail", ItemArgument.item(context))
              .executes(c -> broadcastMail(c, ItemArgument.getItem(c, "mail"), Optional.empty()));
        broadcastMailArgument.then(Commands.literal("from")
              .then(Commands.argument("sender", CompoundTagArgument.compoundTag())
                    .executes(EnvelopeCommand::broadcastMailFrom)));

        return Commands.literal("mail")
              .then(Commands.literal("send").then(mailArgument))
              .then(Commands.literal("broadcast").then(broadcastMailArgument));
    }

    // -- Mail

    private static int sendMail(CommandContext<CommandSourceStack> context, ItemInput item,
                                Optional<Address> sender, Optional<Address> target) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        ItemStack mail = item.createItemStack(1, false);

        Address recipient = target.orElseGet(() -> Mail.getRecipientOrUnknown(mail));

        if (mail.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Cannot send: mail is empty."));
            return 0;
        }

        if (recipient.equals(Address.UNKNOWN)) {
            context.getSource().sendFailure(Component.literal("Cannot send: recipient is not defined."));
            return 0;
        }

        Mail.setRecipient(mail, recipient);

        MailService.of(level).getDeliveryManager()
              .startService(Delivery.draft()
                    .deliver(mail)
                    .from(sender.orElse(Address.UNKNOWN))
                    .to(recipient));

        Component message = Component.literal("Mail sent to ").append(recipient.format().asRecipient().toComponent());
        context.getSource().sendSuccess(() -> message, true);

        return 0;
    }

    private static int sendMailTo(CommandContext<CommandSourceStack> context, Optional<Address> sender) throws CommandSyntaxException {
        return sendMail(context,
              ItemArgument.getItem(context, "mail"),
              sender,
              Optional.of(parseAddress(context, CompoundTagArgument.getCompoundTag(context, "recipient"))));
    }

    private static int sendMailFrom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return sendMail(context,
              ItemArgument.getItem(context, "mail"),
              Optional.of(parseAddress(context, CompoundTagArgument.getCompoundTag(context, "sender"))),
              Optional.empty());
    }

    private static int broadcastMail(CommandContext<CommandSourceStack> context, ItemInput item,
                                     Optional<Address> sender) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        ItemStack mail = item.createItemStack(1, false);

        if (mail.isEmpty() || !mail.is(Envelope.Tags.Items.MAILABLE)) {
            context.getSource().sendFailure(Component.literal("Cannot broadcast: item is not sendable mail."));
            return 0;
        }

        MailService service = MailService.of(level);
        Address from = sender.orElse(Address.UNKNOWN);
        int sent = 0;

        for (PlayerAddress recipient : service.getKnownPlayers().getDefaultAddresses().keySet()) {
            if (recipient.equals(from) || recipient.resolve(service).isUnknown()) {
                continue;
            }

            ItemStack copy = mail.copy();
            Mail.setRecipient(copy, recipient);
            service.getDeliveryManager().startService(Delivery.draft()
                  .deliver(copy)
                  .from(from)
                  .to(recipient));
            sent++;
        }

        if (sent == 0) {
            context.getSource().sendFailure(Component.literal("Cannot broadcast: there are no eligible recipients."));
            return 0;
        }

        int recipientCount = sent;
        context.getSource().sendSuccess(() -> Component.literal("Broadcast sent to " + recipientCount + " recipients."), true);
        return recipientCount;
    }

    private static int broadcastMailFrom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return broadcastMail(context,
              ItemArgument.getItem(context, "mail"),
              Optional.of(parseAddress(context, CompoundTagArgument.getCompoundTag(context, "sender"))));
    }

    private static Address parseAddress(CommandContext<CommandSourceStack> context, CompoundTag tag) {
        return Address.CODEC.parse(context.getSource().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    // -- Mailbox

    private static int listAllMailboxes(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        Set<BlockAddress> addresses = MailService.of(level).getMailboxes().getAllAddresses();

        if (!addresses.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("All mailboxes:"), true);
            for (BlockAddress address : addresses) {
                context.getSource().sendSuccess(() -> copyableAddressAndPos(address,
                      MailService.of(level).getMailboxes().getPositionOf(address)), true);
            }
        } else {
            context.getSource().sendSuccess(() ->
                  Component.literal("There are no known mailboxes."), true);
        }
        return 0;
    }

    private static int listDefaultMailboxes(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();

        Map<PlayerAddress, BlockAddress> defaultAddresses = MailService.of(level).getKnownPlayers().getDefaultAddresses();

        if (!defaultAddresses.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Default addresses:"), true);

            defaultAddresses.forEach((playerAddress, address) -> {
                Optional<BlockPos> position = MailService.of(level).getMailboxes().getPositionOf(address);
                context.getSource().sendSuccess(() -> Component.literal(playerAddress.getString())
                      .append(" - ")
                      .append(copyableAddressAndPos(address, position)), true);
            });
        } else {
            context.getSource().sendSuccess(() ->
                  Component.literal("There are no default mailboxes."), true);
        }

        return 0;
    }

    private static int mailboxPosition(CommandContext<CommandSourceStack> context, BlockAddress address) {
        ServerLevel level = context.getSource().getLevel();
        if (!MailService.of(level).getMailboxes().exists(address)) {
            context.getSource().sendFailure(address.getComponent().append(" does not exist."));
            return 1;
        }

        MailService.of(level).getMailboxes().getPositionOf(address)
              .ifPresentOrElse(
                    pos -> context.getSource().sendSuccess(() -> copyableAddressAndPos(address, Optional.of(pos)), true),
                    () -> context.getSource().sendFailure(address.getComponent()
                          .append(" does not have a position associated with it.")));
        return 0;
    }

    private static MutableComponent copyableAddressAndPos(Address address, Optional<BlockPos> pos) {
        String name = address.getString();
        String posStr = pos.map(BlockPos::toShortString).orElse("");
        String posToCopy = posStr.replace(",", "");

        return Component.literal(name)
              .withStyle(Style.EMPTY
                    .withColor(Colors.ADDRESS_NEUTRAL)
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Copy Address")
                          .append("\n")
                          .append(Component.literal(name).withStyle(ChatFormatting.GRAY))))
                    .withClickEvent(new ClickEvent.CopyToClipboard(name)))
              .append(Component.literal("@[" + posStr + "]").withStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Copy Position")
                          .append("\n")
                          .append(Component.literal(posToCopy).withStyle(ChatFormatting.GRAY))))
                    .withClickEvent(new ClickEvent.CopyToClipboard(posToCopy))));
    }
}
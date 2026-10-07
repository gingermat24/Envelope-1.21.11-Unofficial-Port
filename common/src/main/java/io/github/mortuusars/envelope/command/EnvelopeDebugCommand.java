package io.github.mortuusars.envelope.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.mortuusars.envelope.util.bugger.test.BuggerTests;
import io.github.mortuusars.envelope.util.bugger.test.TestResults;
import io.github.mortuusars.envelope.util.bugger.test.cases.CourierDeliveryTests;
import io.github.mortuusars.envelope.util.bugger.test.cases.MailCraftingRecipeTests;
import io.github.mortuusars.envelope.util.bugger.test.cases.MailCraftingTests;
import io.github.mortuusars.envelope.util.bugger.test.cases.StackIngredientTests;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.world.mail.delivery.PhysicalCourier;
import io.github.mortuusars.envelope.world.mail.delivery.background.BackgroundCourier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.List;

public class EnvelopeDebugCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> commands() {
        return Commands.literal("debug")
              .then(Commands.literal("terminate_all_deliveries")
                    .executes(EnvelopeDebugCommand::confirmTermination)
                    .then(Commands.literal("confirm")
                          .executes(EnvelopeDebugCommand::terminateAllDeliveries)))
              .then(Commands.literal("expire_all_awaiting_payback")
                    .executes(EnvelopeDebugCommand::timeoutAllPaybackMail))
              .then(Commands.literal("run_tests")
                    .executes(EnvelopeDebugCommand::runBuggerTests))
              .then(Commands.literal("test")
                    .executes(EnvelopeDebugCommand::test));
    }

    private static int confirmTermination(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal(
              "This stops all deliveries and voids carried mail. Run /envelope debug terminate_all_deliveries confirm to continue."), true);
        return 0;
    }

    private static int terminateAllDeliveries(CommandContext<CommandSourceStack> context) {
        int terminated = 0;

        for (ServerLevel level : context.getSource().getServer().getAllLevels()) {
            var backgroundDelivery = MailService.of(level).getBackgroundDelivery();
            for (BackgroundCourier courier : List.copyOf(backgroundDelivery.getActiveCouriers())) {
                backgroundDelivery.removeCourier(courier);
                terminated++;
            }

            for (LivingEntity entity : level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
                  entity -> entity instanceof PhysicalCourier courier && courier.isDelivering())) {
                entity.discard();
                terminated++;
            }
        }

        int count = terminated;
        if (count == 0) {
            context.getSource().sendSuccess(() -> Component.literal("There are no active deliveries."), true);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Terminated " + count + " active deliveries."), true);
        }
        return count;
    }

    private static int timeoutAllPaybackMail(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        MailService service = MailService.of(level);
        int returnedCount = service.getPaybackDepartment().returnAllAwaitingAsTimedOut();

        if (returnedCount > 0) {
            context.getSource().sendSuccess(() -> Component.literal("Returned " +
                  returnedCount + " mail awaiting payback."), true);
        } else {
            context.getSource().sendFailure(Component.literal("No mail awaiting payback is returned."));
        }

        return 0;
    }

    private static int runBuggerTests(CommandContext<CommandSourceStack> context) {
        TestResults testResults = new BuggerTests()
              .add(new StackIngredientTests(context.getSource().getServer()))
              .add(new CourierDeliveryTests(context.getSource().getServer()))
              .add(new MailCraftingRecipeTests(context.getSource().getServer()))
              .add(new MailCraftingTests(context.getSource().getServer()))
              .run(count -> context.getSource().sendSuccess(() ->
                    Component.literal("Running " + count + " bugger tests."), true));

        context.getSource().sendSuccess(() -> Component.literal("Bugger tests finished:"), true);

        if (testResults.failed().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("All tests are passed!")
                  .withStyle(ChatFormatting.GREEN), true);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Passed: " + testResults.passed().size() + "\n"), true);
            context.getSource().sendSuccess(() -> Component.literal("Failed: " + testResults.failed().size() + ":")
                  .withStyle(ChatFormatting.RED), true);

            testResults.failed().forEach(failedTest -> {
                context.getSource().sendSuccess(() -> Component.literal(" " + failedTest.name() + ": " + failedTest.error())
                      .withStyle(ChatFormatting.RED), true);
            });
        }

        return 0;
    }

    private static int test(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().getPlayerOrException();
        return 0;
    }
}

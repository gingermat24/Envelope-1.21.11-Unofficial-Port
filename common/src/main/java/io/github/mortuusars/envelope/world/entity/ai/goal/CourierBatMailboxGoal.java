package io.github.mortuusars.envelope.world.entity.ai.goal;

import io.github.mortuusars.envelope.world.block.mailbox.MailboxBlockEntity;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import io.github.mortuusars.envelope.world.entity.ai.PigeonNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CourierBatMailboxGoal extends Goal {
    private final CourierBat bat;
    private BlockPos target;

    public CourierBatMailboxGoal(CourierBat bat) {
        this.bat = bat;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        target = bat.getMailboxHandler().getTargetPos();
        return target != null && !bat.isDelivering() && bat.canStartDelivery()
              && bat.level().getBlockEntity(target) instanceof MailboxBlockEntity mailbox
              && mailbox.isAvailableForPickup();
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !bat.isDelivering() && bat.canStartDelivery()
              && bat.getMailboxHandler().getTargetPos() != null;
    }

    @Override
    public void stop() {
        bat.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null || !(bat.level() instanceof ServerLevel)) {
            return;
        }

        BlockPos approachTarget = PigeonNavigation.getMailboxApproachTarget(bat.level(), target);
        if (PigeonNavigation.hasReachedTarget(bat, approachTarget, PigeonNavigation.getReachDistance() + 1.0)) {
            if (bat.level().getBlockEntity(target) instanceof MailboxBlockEntity mailbox
                  && mailbox.tryStartDelivery(bat)) {
                bat.getMailboxHandler().setTargetPos(null);
                target = null;
            } else {
                bat.getMailboxHandler().dropMailbox();
                target = null;
            }
            return;
        }

        BlockPos navigationPos = PigeonNavigation.getNavigationPos(bat, approachTarget);
        if (!bat.getNavigation().isInProgress() || !navigationPos.equals(bat.getNavigation().getTargetPos())) {
            bat.getNavigation().moveTo(navigationPos.getX(), navigationPos.getY(), navigationPos.getZ(), 1.2);
        }
    }
}

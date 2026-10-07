package io.github.mortuusars.envelope.world.entity.ai.goal;

import io.github.mortuusars.envelope.world.entity.CourierBat;
import io.github.mortuusars.envelope.world.entity.ai.PigeonNavigation;
import io.github.mortuusars.envelope.world.mail.delivery.Delivery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CourierBatDeliverMailGoal extends Goal {
    private final CourierBat bat;

    public CourierBatDeliverMailGoal(CourierBat bat) {
        this.bat = bat;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return bat.isDelivering();
    }

    @Override
    public void stop() {
        bat.getNavigation().stop();
        bat.getNavigation().resetMaxVisitedNodesMultiplier();
    }

    @Override
    public void tick() {
        if (!(bat.level() instanceof ServerLevel level)) {
            return;
        }

        bat.getCurrentDelivery().ifPresent(delivery -> {
            bat.tickDelivery(level, delivery);
            delivery.getRoute().getSegment(delivery.getPhase()).endPos()
                  .ifPresentOrElse(localPos -> {
                      BlockPos target = PigeonNavigation.getSegmentApproachTarget(level, localPos, delivery.getPhase());
                      if ((delivery.getPhase().isAscending() || delivery.getPhase().isDescending())
                            && PigeonNavigation.hasReachedTarget(bat, target, PigeonNavigation.getReachDistance())) {
                          delivery.setPhaseProgress(bat.getPhaseDuration(level, delivery, delivery.getPhase()));
                          return;
                      }

                      if (!PigeonNavigation.hasReachedTarget(bat, target, PigeonNavigation.getReachDistance())) {
                          BlockPos navigationPos = PigeonNavigation.getNavigationPos(bat, target);
                          if (!bat.getNavigation().isInProgress()
                                || !navigationPos.equals(bat.getNavigation().getTargetPos())) {
                              bat.getNavigation().setMaxVisitedNodesMultiplier(10.0F);
                              bat.getNavigation().moveTo(navigationPos.getX(), navigationPos.getY(), navigationPos.getZ(), 1.2);
                          }
                      }
                  }, () -> {
                      @Nullable Vec3 randomPos = AirAndWaterRandomPos.getPos(bat, 8, 4, -2,
                            bat.getX(), bat.getZ(), (float) (Math.PI / 2));
                      if (randomPos != null && level.getRandom().nextFloat() < 0.1f) {
                          bat.getNavigation().moveTo(randomPos.x, randomPos.y, randomPos.z, 1.2);
                      }
                  });
        });
    }
}

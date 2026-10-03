package io.github.mortuusars.envelope.mixin.villager_feeds_pigeon;

import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.VillagerPigeonFeeding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager
      implements ReputationEventHandler, VillagerDataHolder, VillagerPigeonFeeding.FeedingVillager {
    @Unique
    public int envelope$pigeonFoodPickupDelay = 0; // Villagers pick up the seeds themselves, so we need to block that when feeding
    @Unique
    public int envelope$pigeonFeedCooldown = 0;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void envelope$addPigeonFoodPickupDelay(int delay) {
        envelope$pigeonFoodPickupDelay = delay;
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void onCustomServerAiStep(ServerLevel serverLevel, CallbackInfo ci) {
        Villager villager = ((Villager) (Object) this);

        if (envelope$pigeonFoodPickupDelay > 0) envelope$pigeonFoodPickupDelay--;
        if (envelope$pigeonFeedCooldown > 0) envelope$pigeonFeedCooldown--;

        if (Config.Server.VILLAGER_FEEDING_PIGEONS.get()
              && envelope$pigeonFeedCooldown <= 0
              && VillagerPigeonFeeding.tryFeed(villager)) {
            envelope$pigeonFeedCooldown = VillagerPigeonFeeding.getFeedCooldown(villager);
        }
    }

    @Inject(method = "wantsToPickUp", at = @At("HEAD"), cancellable = true)
    private void wantsToPickUp(ServerLevel serverLevel, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (envelope$pigeonFoodPickupDelay > 0 && stack.is(Envelope.Tags.Items.PIGEON_FOOD)) {
            cir.setReturnValue(false);
        }
    }

    // --

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void save(ValueOutput output, CallbackInfo ci) {
        if (envelope$pigeonFoodPickupDelay > 0) output.putInt("EnvelopeItemPickupDelay", envelope$pigeonFoodPickupDelay);
        if (envelope$pigeonFeedCooldown > 0) output.putInt("EnvelopePigeonFeedCooldown", envelope$pigeonFeedCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void load(ValueInput input, CallbackInfo ci) {
        envelope$pigeonFoodPickupDelay = input.getIntOr("EnvelopeItemPickupDelay", 0);
        envelope$pigeonFeedCooldown = input.getIntOr("EnvelopePigeonFeedCooldown", 0);
    }
}

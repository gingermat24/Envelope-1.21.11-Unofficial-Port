package io.github.mortuusars.envelope.world.entity;

import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.Position;
import io.github.mortuusars.envelope.world.entity.ai.MailboxHandler;
import io.github.mortuusars.envelope.world.entity.ai.PigeonNavigation;
import io.github.mortuusars.envelope.world.entity.ai.goal.CourierBatDeliverMailGoal;
import io.github.mortuusars.envelope.world.entity.ai.goal.CourierBatMailboxGoal;
import io.github.mortuusars.envelope.world.entity.spawning.SpawnableEntityData;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.world.mail.delivery.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CourierBat extends PathfinderMob implements FlyingAnimal, PhysicalCourier {
    public static final List<String> IGNORED_TAGS = Arrays.asList(
          "Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance",
          "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded",
          "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "SleepingX", "SleepingY",
          "SleepingZ", "Passengers", "UUID", "leash", "Delivery"
    );

    private static final EntityDataAccessor<Boolean> DATA_DELIVERING =
          SynchedEntityData.defineId(CourierBat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_MAIL =
          SynchedEntityData.defineId(CourierBat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SERVICE =
          SynchedEntityData.defineId(CourierBat.class, EntityDataSerializers.BOOLEAN);

    private MailboxHandler mailboxHandler = new MailboxHandler();
    private @Nullable Delivery delivery;
    private @Nullable CourierOrigin origin;
    private @Nullable BlockPos spawnPos;

    public CourierBat(EntityType<? extends CourierBat> entityType, Level level) {
        super(entityType, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
              .add(Attributes.MAX_HEALTH, 6.0)
              .add(Attributes.FLYING_SPEED, 1.0)
              .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    public static CourierBat createService(ServerLevel level) {
        return Envelope.EntityTypes.COURIER_BAT.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }

    public static Courier spawnServiceCourier(ServerLevel level, Delivery delivery) {
        CourierBat bat = createService(level);
        if (bat == null) {
            throw new IllegalStateException("Could not create a service Courier Bat.");
        }
        bat.setOrigin(CourierOrigin.service());
        bat.startDelivery(delivery);
        return bat.transitionToBackground(level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DELIVERING, false);
        builder.define(DATA_HAS_MAIL, false);
        builder.define(DATA_SERVICE, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new CourierBatDeliverMailGoal(this));
        goalSelector.addGoal(1, new CourierBatMailboxGoal(this));
        WaterAvoidingRandomFlyingGoal wanderGoal = new WaterAvoidingRandomFlyingGoal(this, 1.0);
        wanderGoal.setInterval(1);
        goalSelector.addGoal(2, wanderGoal);
        goalSelector.addGoal(3, new FloatGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        mailboxHandler.tick(this, level());
        if (level() instanceof ServerLevel && !isDelivering()
              && mailboxHandler.getTargetPos() == null && tickCount % 20 == 0) {
            if (isService()) {
                discard();
            } else {
                convertTo(EntityType.BAT, ConversionParams.single(this, true, true), EntitySpawnReason.CONVERSION, ignored -> { });
            }
        }
    }

    @Override
    protected @NotNull FlyingPathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    public void checkDespawn() {
        if (level() instanceof ServerLevel serverLevel && !isNoAi() && isDelivering()
              && !Position.isInSimulationDistance(serverLevel, this)) {
            transitionToBackground(serverLevel);
            return;
        }
        super.checkDespawn();
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || isDelivering();
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    protected float getFlyingSpeed() {
        return 0.04f;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.1f;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95f;
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        return SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected boolean shouldDropLoot(ServerLevel serverLevel) {
        return super.shouldDropLoot(serverLevel) && (origin == null || !origin.isService());
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel serverLevel, DamageSource damageSource) {
        super.dropAllDeathLoot(serverLevel, damageSource);
        getCurrentDelivery().ifPresent(delivery -> {
            if (!getOrigin().isService()) {
                MailService.of(serverLevel).sendCourierDeathNotice(this, delivery, damageSource);
            }
            if (!delivery.getMail().isEmpty()) {
                ItemStack mail = delivery.getPhase().isOnRecipientSide()
                      ? Mail.asDelivered(delivery.getMail())
                      : delivery.getMail();
                spawnAtLocation(serverLevel, mail);
                delivery.setMail(ItemStack.EMPTY);
            }
        });
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (isDeadOrDying()) {
            return false;
        }
        if (getRandom().nextDouble() < Config.Server.DELIVERY_COURIER_DAMAGE_EVASION_CHANCE.get()
              && !source.is(Envelope.Tags.DamageTypes.BYPASSES_COURIER_DELIVERY_EVASION)) {
            serverLevel.sendParticles(ParticleTypes.POOF, position().x, position().y, position().z,
                  3, 0.3, 0.3, 0.3, 0);
            serverLevel.playSound(null, this, SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 1,
                  getRandom().nextFloat() * 0.1f + 0.95f);
            return false;
        }
        return super.hurtServer(serverLevel, source, amount);
    }

    public MailboxHandler getMailboxHandler() {
        return mailboxHandler;
    }

    public void setMailboxHandler(MailboxHandler mailboxHandler) {
        this.mailboxHandler = mailboxHandler;
    }

    public boolean hasMail() {
        return entityData.get(DATA_HAS_MAIL);
    }

    public boolean isService() {
        return entityData.get(DATA_SERVICE);
    }

    public @Nullable BlockPos getSpawnPos() {
        return spawnPos;
    }

    public void setSpawnPos(@Nullable BlockPos spawnPos) {
        this.spawnPos = spawnPos;
    }

    public boolean canStartDelivery() {
        return isNight(level()) && !level().isRaining() && !level().isThundering();
    }

    public static boolean isNight(Level level) {
        long timeOfDay = Math.floorMod(level.getDayTime(), 24000L);
        return timeOfDay >= 13000L && timeOfDay < 23000L;
    }

    public CourierBat startDelivery(Delivery delivery) {
        if (origin == null) {
            setOrigin(CourierOrigin.regular(blockPosition()));
        }
        setDelivery(delivery);
        return this;
    }

    @Override
    public double getDeliveryTravelSpeed() {
        return Config.Server.DELIVERY_BAT_TRAVEL_SPEED.get();
    }

    @Override
    public boolean isBatCourier() {
        return true;
    }

    @Override
    public boolean isTimeSwitchableCourier() {
        return true;
    }

    @Override
    public Optional<Delivery> getCurrentDelivery() {
        return Optional.ofNullable(delivery);
    }

    @Override
    public void setDelivery(@Nullable Delivery delivery) {
        if (this.delivery == null && delivery == null) {
            return;
        }
        this.delivery = delivery;
        entityData.set(DATA_DELIVERING, delivery != null);
        entityData.set(DATA_HAS_MAIL, delivery != null && !delivery.getMail().isEmpty());
    }

    @Override
    public @NotNull CourierOrigin getOrigin() {
        if (origin == null) {
            origin = CourierOrigin.regular(blockPosition());
        }
        return origin;
    }

    public void setOrigin(@Nullable CourierOrigin origin) {
        this.origin = origin;
        entityData.set(DATA_SERVICE, origin != null && origin.isService());
    }

    @Override
    public SpawnableEntityData toSpawnableData() {
        return SpawnableEntityData.of(this, IGNORED_TAGS);
    }

    @Override
    public int getPhaseDuration(ServerLevel level, Delivery delivery, DeliveryPhase phase) {
        return switch (phase) {
            case DEPARTING_SENDER, APPROACHING_RECIPIENT, DEPARTING_RECIPIENT, APPROACHING_SENDER -> 30 * 20;
            default -> PhysicalCourier.super.getPhaseDuration(level, delivery, phase);
        };
    }

    @Override
    public void phaseStarted(ServerLevel level, Delivery delivery) {
        PhysicalCourier.super.phaseStarted(level, delivery);
        if (delivery.getPhase().isTraveling()) {
            transitionToBackground(level);
        }
    }

    @Override
    public boolean handlePhaseTransition(ServerLevel level, Delivery delivery) {
        if ((delivery.getPhase() == DeliveryPhase.DEPARTING_SENDER
              || delivery.getPhase() == DeliveryPhase.APPROACHING_RECIPIENT)
              && !hasReachedSegmentEndPos(delivery)) {
            Mail.returned(delivery.getMail(), io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryRecord.Message.UNABLE_TO_REACH);
            delivery.beginPhase(delivery.getPhase() == DeliveryPhase.DEPARTING_SENDER
                  ? DeliveryPhase.APPROACHING_SENDER
                  : DeliveryPhase.DEPARTING_RECIPIENT);
            return true;
        }
        return PhysicalCourier.super.handlePhaseTransition(level, delivery);
    }

    private boolean hasReachedSegmentEndPos(Delivery delivery) {
        return delivery.getRoute().getSegment(delivery.getPhase()).endPos()
              .map(endPos -> PigeonNavigation.hasReachedTarget(this,
                    PigeonNavigation.getSegmentApproachTarget(level(), endPos, delivery.getPhase()),
                    PigeonNavigation.getReachDistance()))
              .orElse(true);
    }

    @Override
    public void endDelivery(ServerLevel level, Delivery delivery) {
        playAmbientSound();
        if (!delivery.getMail().isEmpty()) {
            spawnAtLocation(level, delivery.getMail().copy());
            delivery.setMail(ItemStack.EMPTY);
        }
        setDelivery(null);
        if (getOrigin().isService()) {
            onVanished(level);
            discard();
            return;
        }

        convertTo(EntityType.BAT, ConversionParams.single(this, true, true), EntitySpawnReason.CONVERSION, ignored -> { });
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("MailboxHandler", MailboxHandler.CODEC, mailboxHandler);
        if (delivery != null) {
            output.store("Delivery", Delivery.CODEC, delivery);
        }
        if (origin != null) {
            output.store("Origin", CourierOrigin.CODEC, origin);
        }
        if (spawnPos != null) {
            output.store("SpawnPos", BlockPos.CODEC, spawnPos);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("MailboxHandler", MailboxHandler.CODEC).ifPresent(this::setMailboxHandler);
        setDelivery(input.read("Delivery", Delivery.CODEC).orElse(null));
        setOrigin(input.read("Origin", CourierOrigin.CODEC).orElse(null));
        spawnPos = input.read("SpawnPos", BlockPos.CODEC).orElse(null);
        entityData.set(DATA_DELIVERING, delivery != null);
        entityData.set(DATA_HAS_MAIL, delivery != null && !delivery.getMail().isEmpty());
    }
}

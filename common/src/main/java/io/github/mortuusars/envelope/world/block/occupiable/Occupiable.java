package io.github.mortuusars.envelope.world.block.occupiable;

import io.github.mortuusars.envelope.Envelope;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.ProblemReporter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface Occupiable {
    List<String> IGNORED_OCCUPANT_TAGS = Arrays.asList(
          "Air",
          "ArmorDropChances",
          "ArmorItems",
          "Brain",
          "CanPickUpLoot",
          "DeathTime",
          "FallDistance",
          "FallFlying",
          "Fire",
          "HandDropChances",
          "HandItems",
          "HurtByTimestamp",
          "HurtTime",
          "LeftHanded",
          "Motion",
          "NoGravity",
          "OnGround",
          "PortalCooldown",
          "Pos",
          "Rotation",
          "SleepingX",
          "SleepingY",
          "SleepingZ",
          "Passengers",
          "UUID",
          "leash"
    );

    boolean canBeOccupiedBy(Entity entity);

    List<Occupant.Mutable> getOccupants();

    SoundEvent getOccupantEnterSound(Entity entity);

    SoundEvent getOccupantExitSound(Entity entity);

    SoundEvent getOccupantWorkSound();

    void playSound(SoundEvent soundEvent, float volume, float pitch);

    default List<Occupant> getImmutableOccupants() {
        return getOccupants().stream().map(Occupant.Mutable::toImmutable).toList();
    }

    default int getMaxOccupantsCount() {
        return 3;
    }

    default boolean hasSpaceForAnotherOccupant() {
        return getOccupants().size() < getMaxOccupantsCount();
    }

    default int getMinimumTicksInsideForOccupant(Entity entity) {
        return 600;
    }

    default void addOccupant(BlockPos pos, BlockState state, Entity entity) {
        if (!hasSpaceForAnotherOccupant() || !canBeOccupiedBy(entity)) return;

        entity.stopRiding();
        entity.ejectPassengers();

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
        if (!entity.save(output)) {
            Envelope.LOGGER.error("Failed to save occupant entity '{}' before entering occupiable block at {}.", entity, pos);
            return;
        }
        CompoundTag tag = output.buildResult();
        cleanupOccupantEntityTag(tag);
        getOccupants().add(new Occupant(CustomData.of(tag), getFirstFreeSlotForOccupant(),
              getMinimumTicksInsideForOccupant(entity), 0).toMutable());

        entity.level().gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, state));
        playSound(getOccupantEnterSound(entity), 1.0F, entity.level().getRandom().nextFloat() * 0.2F + 0.85F);
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CLOUD,
                  entity.getX(), entity.getY(), entity.getZ(), 1, 0.2f, 0.2f, 0.2f, 0);
        }

        entity.discard();
        onOccupantsChanged();
    }

    default Optional<Entity> releaseOccupant(Level level, BlockPos pos, BlockState state, Occupant occupant, ReleaseReason reason) {
        if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();
        if ((level.getDayTime() % 24000L >= 13000L || level.isRaining() || level.isThundering()) && reason != ReleaseReason.EMERGENCY) {
            return Optional.empty();
        }

        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos releasePos = pos.relative(direction);

        boolean isFrontBlockedOff = !level.getBlockState(releasePos).getCollisionShape(level, releasePos).isEmpty();
        if (isFrontBlockedOff && reason != ReleaseReason.EMERGENCY) {
            return Optional.empty();
        }

        @Nullable Entity entity = createEntityFromOccupant(level, occupant, pos);
        if (entity == null) return Optional.empty();

        double offset = isFrontBlockedOff ? 0.0 : 0.55 + (double) (entity.getBbWidth() / 2.0F);
        double x = (double) pos.getX() + 0.5 + offset * (double) direction.getStepX();
        double y = (double) pos.getY() + 0.5 - (double) (entity.getBbHeight() / 2.0F);
        double z = (double) pos.getZ() + 0.5 + offset * (double) direction.getStepZ();
        entity.snapTo(x, y, z, entity.getYRot(), entity.getXRot());

        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, state));
        playSound(getOccupantExitSound(entity), 1.0F, entity.level().getRandom().nextFloat() * 0.2F + 0.85F);
        serverLevel.sendParticles(ParticleTypes.CLOUD,
              entity.getX(), entity.getY() + 0.5f, entity.getZ(), 1, 0.2f, 0.2f, 0.2f, 0);

        if (level.addFreshEntity(entity)) {
            onOccupantReleased(serverLevel, entity, reason);
            return Optional.of(entity);
        }

        return Optional.empty();
    }

    default @Nullable Entity createEntityFromOccupant(Level level, Occupant occupant, BlockPos pos) {
        CompoundTag tag = occupant.entityData().copyTag();
        cleanupOccupantEntityTag(tag);

        @Nullable Entity entity = EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.LOAD, loadedEntity -> loadedEntity);
        if (entity == null || !canBeOccupiedBy(entity)) {
            return null;
        }

        updateEntityAfterRelease(entity, occupant.ticksInside());
        return entity;
    }

    default List<Entity> releaseAllOccupants(Level level, BlockPos pos, BlockState state, ReleaseReason reason) {
        List<Entity> releasedEntities = new ArrayList<>();

        getOccupants().removeIf(occupant ->
              releaseOccupant(level, pos, state, occupant.toImmutable(), reason)
              .map(entity -> {
                  releasedEntities.add(entity);
                  return true;
              }).orElse(false));

        if (!releasedEntities.isEmpty()) {
            onOccupantsChanged();
        }

        return releasedEntities;
    }

    default void onOccupantReleased(Level level, Entity entity, ReleaseReason reason) {
    }

    default void updateEntityAfterRelease(Entity entity, int ticksInside) {
        entity.setNoGravity(true);

        if (entity instanceof Animal animal) {
            int ageTicks = animal.getAge();
            if (ageTicks < 0) {
                animal.setAge(Math.min(0, ageTicks + ticksInside));
            } else if (ageTicks > 0) {
                animal.setAge(Math.max(0, ageTicks - ticksInside));
            }

            animal.setInLoveTime(Math.max(0, animal.getInLoveTime() - ticksInside));
        }
    }

    default void onOccupantsChanged() {
    }

    default void tickOccupants(Level level, BlockPos pos, BlockState state) {
        if (getOccupants().removeIf(occupant -> occupant.tick()
              && releaseOccupant(level, pos, state, occupant.toImmutable(), ReleaseReason.DEFAULT).isPresent())) {
            onOccupantsChanged();
        }

        if (!getOccupants().isEmpty()) {
            if (level.getRandom().nextDouble() < 0.004 * getOccupants().size()) {
                playSound(getOccupantWorkSound(), 1.0F, level.getRandom().nextFloat() * 0.2F + 0.9F);
            }
        }
    }

    // -- Save / Load

    default void cleanupOccupantEntityTag(CompoundTag tag) {
        IGNORED_OCCUPANT_TAGS.forEach(tag::remove);
    }

    default String getSerializedOccupantsName() {
        return "occupants";
    }

    default void saveOccupiable(ValueOutput output) {
        output.store(getSerializedOccupantsName(), Occupant.LIST_CODEC, getImmutableOccupants());
    }

    default void loadOccupiable(ValueInput input) {
        getOccupants().clear();
        input.read(getSerializedOccupantsName(), Occupant.LIST_CODEC)
              .map(list -> list.stream().map(Occupant::toMutable).toList())
              .ifPresent(list -> getOccupants().addAll(list));
    }

    // --

    default int getFirstFreeSlotForOccupant() {
        List<Integer> slots = getImmutableOccupants().stream()
              .map(Occupant::slot)
              .sorted()
              .toList();

        int slot = 0;
        while (true) {
            if (!slots.contains(slot)) {
                return slot;
            }
            slot++;
        }
    }

    enum ReleaseReason {
        DEFAULT,
        EMERGENCY;
    }
}
package io.github.mortuusars.envelope.world.entity.spawning;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public record SpawnableEntityData(CustomData data) {
    public static final Codec<SpawnableEntityData> CODEC =
                CustomData.CODEC.validate(data -> {
                    if (data.isEmpty()) return DataResult.error(() -> "Entity data cannot be empty.");
                    return DataResult.success(data);
                })
          .xmap(SpawnableEntityData::new, SpawnableEntityData::data);

    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("deprecation")
    public SpawnableEntityData {
        Preconditions.checkArgument(!data.isEmpty(), "Entity data cannot be empty.");
        Preconditions.checkState(!data.copyTag().isEmpty(), "Entity tag cannot be empty.");
        Preconditions.checkState(data.copyTag().getString("id").isPresent(),
              "Entity tag does not contain an 'id': " + data.getClass());
    }

    public static SpawnableEntityData of(Entity entity, List<String> ignoredTags) {
        Preconditions.checkArgument(!entity.isPassenger() && !entity.isRemoved() && entity.getType().canSerialize(),
              "Cannot create SpawnableEntityData: entity '" + entity + "' is passenger, removed or the type is not serializable.");

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
        if (!entity.save(output)) {
            LOGGER.error("Failed to save entity '{}' to a tag. Entity is passenger, " +
                  "about to be removed or entity type is not serializable.", entity);
        }

        CompoundTag tag = output.buildResult();
        ignoredTags.forEach(tag::remove);
        return new SpawnableEntityData(CustomData.of(tag));
    }

    // --

    @SuppressWarnings("deprecation")
    public @Nullable Entity createEntity(ServerLevel level) {
        @Nullable Entity entity = EntityType.loadEntityRecursive(
              TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data.copyTag()),
              level, EntitySpawnReason.LOAD, loadedEntity -> loadedEntity);
        if (entity == null) {
            LOGGER.error("Failed to create spawnable entity. Tag: {}", data.copyTag());
        }
        return entity;
    }
}
package io.github.mortuusars.envelope.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.mortuusars.envelope.world.level.saveddata.SealLocks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record SealLock(String owner, UUID id) {
    public static final Codec<SealLock> CODEC = Codec.STRING.comapFlatMap(SealLock::parse, SealLock::toString);
    public static final StreamCodec<RegistryFriendlyByteBuf, SealLock> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.STRING_UTF8, SealLock::owner,
          net.minecraft.core.UUIDUtil.STREAM_CODEC, SealLock::id,
          SealLock::new
    );

    public SealLock {
        if (StringUtil.isNullOrEmpty(owner) || owner.indexOf('#') >= 0) {
            throw new IllegalArgumentException("Seal lock owner must be non-empty and cannot contain '#'.");
        }
    }

    public static SealLock create(String owner) {
        return new SealLock(owner, UUID.randomUUID());
    }

    public static DataResult<SealLock> parse(String value) {
        if (StringUtil.isNullOrEmpty(value)) {
            return DataResult.error(() -> "Seal lock cannot be parsed from an empty string.");
        }

        int separator = value.lastIndexOf('#');
        if (separator < 1 || separator == value.length() - 1) {
            return DataResult.error(() -> "Expected seal lock format 'owner#uuid', got: " + value);
        }

        try {
            return DataResult.success(new SealLock(value.substring(0, separator),
                  UUID.fromString(value.substring(separator + 1))));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid seal lock UUID: " + exception.getMessage());
        }
    }

    @Override
    public String toString() {
        return owner + "#" + id;
    }

    public boolean isOwnedBy(Player player) {
        return owner.equalsIgnoreCase(player.getScoreboardName());
    }

    public boolean isLocked(Level level) {
        return level instanceof ServerLevel serverLevel && SealLocks.get(serverLevel).isLocked(this);
    }

    public boolean isLockedFor(Player player) {
        return !isOwnedBy(player) && isLocked(player.level());
    }

    public boolean lock(Level level) {
        return level instanceof ServerLevel serverLevel && SealLocks.get(serverLevel).lock(this);
    }

    public boolean unlock(Level level) {
        return level instanceof ServerLevel serverLevel && SealLocks.get(serverLevel).unlock(this);
    }

    public static boolean unlockAllFrom(String owner, Level level) {
        return level instanceof ServerLevel serverLevel && SealLocks.get(serverLevel).unlockAllFrom(owner);
    }
}

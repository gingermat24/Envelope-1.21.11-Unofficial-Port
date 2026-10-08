package io.github.mortuusars.envelope.world.level.saveddata;

import com.mojang.serialization.Codec;
import io.github.mortuusars.envelope.world.item.component.SealLock;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SealLocks extends SavedData {
    public static final Codec<SealLocks> CODEC = Codec.list(SealLock.CODEC).xmap(SealLocks::new,
          savedData -> List.copyOf(savedData.locks));

    private final Set<SealLock> locks;

    public SealLocks(List<SealLock> locks) {
        this.locks = new HashSet<>(locks);
    }

    public SealLocks() {
        this(List.of());
    }

    public Set<SealLock> getLocks() {
        return Set.copyOf(locks);
    }

    public boolean isLocked(SealLock lock) {
        return locks.contains(lock);
    }

    public boolean lock(SealLock lock) {
        if (!locks.add(lock)) {
            return false;
        }
        setDirty();
        return true;
    }

    public boolean unlock(SealLock lock) {
        if (!locks.remove(lock)) {
            return false;
        }
        setDirty();
        return true;
    }

    public boolean unlockAllFrom(String owner) {
        if (!locks.removeIf(lock -> lock.owner().equalsIgnoreCase(owner))) {
            return false;
        }
        setDirty();
        return true;
    }

    public static SealLocks get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(type());
    }

    private static SavedDataType<SealLocks> type() {
        return new SavedDataType<>("envelope_seal_locks", SealLocks::new, CODEC, DataFixTypes.LEVEL);
    }
}

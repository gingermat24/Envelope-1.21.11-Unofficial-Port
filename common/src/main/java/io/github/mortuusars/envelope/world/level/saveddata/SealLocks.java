package io.github.mortuusars.envelope.world.level.saveddata;

import com.mojang.serialization.Codec;
import io.github.mortuusars.envelope.world.item.component.SealLock;
import io.github.mortuusars.envelope.network.Packets;
import io.github.mortuusars.envelope.network.packet.clientbound.SyncSealLocksS2CP;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

public class SealLocks extends SavedData {
    public static final Codec<SealLocks> CODEC = Codec.list(SealLock.CODEC).xmap(SealLocks::new,
          savedData -> List.copyOf(savedData.locks));

    private final Set<SealLock> locks;
    private static volatile Set<SealLock> clientLocks = Set.of();

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
        syncToAllClients();
        return true;
    }

    public boolean unlock(SealLock lock) {
        if (!locks.remove(lock)) {
            return false;
        }
        setDirty();
        syncToAllClients();
        return true;
    }

    public boolean unlockAllFrom(String owner) {
        if (!locks.removeIf(lock -> lock.owner().equalsIgnoreCase(owner))) {
            return false;
        }
        setDirty();
        syncToAllClients();
        return true;
    }

    public void syncToAllClients() {
        Packets.sendToAllClients(new SyncSealLocksS2CP(List.copyOf(locks)));
    }

    public void syncToClient(ServerPlayer player) {
        Packets.sendToClient(new SyncSealLocksS2CP(List.copyOf(locks)), player);
    }

    public static boolean isClientLocked(SealLock lock) {
        return clientLocks.contains(lock);
    }

    public static void setClientLocks(Collection<SealLock> locks) {
        clientLocks = Set.copyOf(locks);
    }

    public static void clearClientLocks() {
        clientLocks = Set.of();
    }

    public static SealLocks get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(type());
    }

    private static SavedDataType<SealLocks> type() {
        return new SavedDataType<>("envelope_seal_locks", SealLocks::new, CODEC, DataFixTypes.LEVEL);
    }
}

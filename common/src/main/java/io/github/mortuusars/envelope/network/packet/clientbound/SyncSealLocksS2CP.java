package io.github.mortuusars.envelope.network.packet.clientbound;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.network.packet.Packet;
import io.github.mortuusars.envelope.world.item.component.SealLock;
import io.github.mortuusars.envelope.world.level.saveddata.SealLocks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncSealLocksS2CP(List<SealLock> locks) implements Packet {
    public static final Identifier ID = Envelope.resource("sync_seal_locks");
    public static final Type<SyncSealLocksS2CP> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSealLocksS2CP> STREAM_CODEC =
          SealLock.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SyncSealLocksS2CP::new, SyncSealLocksS2CP::locks);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketFlow flow, Player player) {
        SealLocks.setClientLocks(locks);
        return true;
    }
}

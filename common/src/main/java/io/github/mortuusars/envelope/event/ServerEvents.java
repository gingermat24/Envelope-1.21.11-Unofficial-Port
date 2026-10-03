package io.github.mortuusars.envelope.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

public class ServerEvents {
    public static void serverStarted(MinecraftServer server) {
    }

    public static void serverTick(MinecraftServer server) {
    }

    public static void playerLogin(ServerPlayer player) {
        ((ServerLevel) player.level()).getEnvelopeMailService().getKnownPlayers().add(player);
    }
}

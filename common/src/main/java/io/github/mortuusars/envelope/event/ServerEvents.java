package io.github.mortuusars.envelope.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.gamerules.GameRules;

public class ServerEvents {
    public static void serverStarted(MinecraftServer server) {
    }

    public static void serverTick(MinecraftServer server) {
    }

    public static void playerLogin(ServerPlayer player) {
        ((ServerLevel) player.level()).getEnvelopeMailService().getKnownPlayers().add(player);
    }

    public static void playerCopy(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (!alive && !oldPlayer.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            for (int slot = 0; slot < oldPlayer.getInventory().getContainerSize(); slot++) {
                ItemStack stack = oldPlayer.getInventory().getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                if (customData.copyTag().contains("envelope_keep_in_inventory_on_transfer")) {
                    customData = customData.update(tag -> tag.remove("envelope_keep_in_inventory_on_transfer"));
                    if (customData.isEmpty()) {
                        stack.remove(DataComponents.CUSTOM_DATA);
                    } else {
                        stack.set(DataComponents.CUSTOM_DATA, customData);
                    }
                    newPlayer.getInventory().setItem(slot, stack);
                }
            }
        }
    }
}

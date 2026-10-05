package io.github.mortuusars.envelope.world.item.component.seal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SealTests {
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void codecRoundTripsPlayerUuid() {
        UUID playerUuid = UUID.fromString("cbf32bda-5479-4bd4-b8b5-9b3139e8a15a");
        Seal seal = createSeal(Optional.of(playerUuid));

        JsonElement encoded = Seal.CODEC.encodeStart(JsonOps.INSTANCE, seal).getOrThrow();
        Seal decoded = Seal.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(Optional.of(playerUuid), decoded.playerUuid());
        assertEquals(seal.signature().getString(), decoded.signature().getString());
    }

    @Test
    void legacySealWithoutUuidStillDecodes() {
        JsonObject encoded = Seal.CODEC.encodeStart(JsonOps.INSTANCE, createSeal(Optional.empty()))
              .getOrThrow().getAsJsonObject();
        encoded.remove("player_id");

        Seal decoded = Seal.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(Optional.empty(), decoded.playerUuid());
        assertEquals("Test Signer", decoded.signature().getString());
    }

    private static Seal createSeal(Optional<UUID> playerUuid) {
        SealMaterial material = new SealMaterial(Identifier.fromNamespaceAndPath("envelope", "test"),
              0xFFFFFFFF, ShadingPalette.IRON_DIE);
        SealSymbol impression = new SealSymbol(Identifier.fromNamespaceAndPath("envelope", "test"));
        return new Seal(Holder.direct(material), Holder.direct(impression), Component.literal("Test Signer"), playerUuid);
    }
}

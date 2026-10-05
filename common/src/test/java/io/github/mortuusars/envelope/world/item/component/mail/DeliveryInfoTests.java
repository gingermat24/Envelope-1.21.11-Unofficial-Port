package io.github.mortuusars.envelope.world.item.component.mail;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryLog;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryRecord;
import io.github.mortuusars.envelope.world.mail.address.Address;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryInfoTests {
    @Test
    void codecRoundTripsDeliveryInformation() {
        DeliveryLog log = DeliveryLog.EMPTY.append(DeliveryRecord.sentFrom(Address.UNKNOWN, 42));
        DeliveryInfo info = new DeliveryInfo(Optional.of(Address.UNKNOWN), log, true);

        JsonElement encoded = DeliveryInfo.CODEC.encodeStart(JsonOps.INSTANCE, info).getOrThrow();

        assertEquals(info, DeliveryInfo.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
    }

    @Test
    void legacyDataCreatesEquivalentDeliveryInformation() {
        DeliveryLog log = DeliveryLog.EMPTY.append(DeliveryRecord.sentFrom(Address.UNKNOWN, 42));

        DeliveryInfo info = DeliveryInfo.fromLegacy(Address.UNKNOWN, log, true);

        assertEquals(Optional.of(Address.UNKNOWN), info.sender());
        assertEquals(log, info.log());
        assertTrue(info.isReturned());
    }

    @Test
    void missingLegacyValuesUseEmptyDefaults() {
        DeliveryInfo info = DeliveryInfo.fromLegacy(null, null, false);
        assertEquals(DeliveryInfo.EMPTY, info);
        assertFalse(info.isReturned());
    }
}

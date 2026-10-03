package io.github.mortuusars.envelope.world.mail.payback;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.world.item.component.Id;
import io.github.mortuusars.envelope.world.item.component.PaybackSubject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PaybackDepartmentData extends SavedData {
    public static final Codec<PaybackDepartmentData> CODEC = RecordCodecBuilder.create(i -> i.group(
          Codec.unboundedMap(Id.CODEC, PaybackSubject.CODEC)
                .optionalFieldOf("payback_pending", Collections.emptyMap())
                .forGetter(PaybackDepartmentData::getPaybackPendingSubjects)
    ).apply(i, PaybackDepartmentData::new));

    private final Map<Id, PaybackSubject> paybackPendingSubjects;

    public PaybackDepartmentData(Map<Id, PaybackSubject> paybackPendingSubjects) {
        this.paybackPendingSubjects = new HashMap<>(paybackPendingSubjects); // Make sure it's mutable
    }

    public PaybackDepartmentData() {
        this.paybackPendingSubjects = new HashMap<>();
    }

    public Map<Id, PaybackSubject> getPaybackPendingSubjects() {
        return paybackPendingSubjects;
    }

    // -- Save / Load

    public static PaybackDepartmentData get(ServerLevel level, String name) {
        return level.getDataStorage().computeIfAbsent(type(name));
    }

    private static SavedDataType<PaybackDepartmentData> type(String name) {
        return new SavedDataType<>(name, PaybackDepartmentData::new, CODEC, DataFixTypes.LEVEL);
    }
}

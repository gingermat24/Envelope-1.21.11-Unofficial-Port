package io.github.mortuusars.envelope.world.mail.dropoff;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class MailDropOffHandlerSavedData extends SavedData {
    public static final Codec<MailDropOffHandlerSavedData> CODEC = CompoundTag.CODEC.xmap(MailDropOffHandlerSavedData::new, MailDropOffHandlerSavedData::getData);

    protected final CompoundTag data;

    public MailDropOffHandlerSavedData(CompoundTag data) {
        this.data = data;
    }

    public MailDropOffHandlerSavedData() {
        this.data = new CompoundTag();
    }

    public CompoundTag getData() {
        return data;
    }

    public CompoundTag get(Identifier location) {
        return getData().getCompoundOrEmpty(location.toString());
    }

    public void set(Identifier location, CompoundTag data) {
        getData().put(location.toString(), data);
        setDirty();
    }

    // -- Save / Load

    public static MailDropOffHandlerSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    private static final SavedDataType<MailDropOffHandlerSavedData> TYPE =
          new SavedDataType<>("envelope_mail_dropoff_handlers", MailDropOffHandlerSavedData::new, CODEC, DataFixTypes.LEVEL);
}

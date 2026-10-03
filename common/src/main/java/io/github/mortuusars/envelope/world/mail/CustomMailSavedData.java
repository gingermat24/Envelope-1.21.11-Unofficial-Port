package io.github.mortuusars.envelope.world.mail;

import com.mojang.serialization.Codec;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.mail.address.type.ServiceAddress;
import io.github.mortuusars.envelope.world.mail.service.ServiceAddressDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class CustomMailSavedData extends SavedData {
    public static final Codec<CustomMailSavedData> CODEC = CompoundTag.CODEC.xmap(CustomMailSavedData::new, CustomMailSavedData::getData);

    protected final CompoundTag data;

    public CustomMailSavedData(CompoundTag data) {
        this.data = data;
    }

    public CustomMailSavedData() {
        this.data = new CompoundTag();
    }

    public CompoundTag getData() {
        return data;
    }

    /**
     * Gets the tag by ID.<br>
     * Don't forget to call {@link #setDirty()} or {@link #set(Identifier, CompoundTag)} to ensure that changes will be saved.
     * @param location ID of the data.
     */
    public CompoundTag get(Identifier location) {
        return getData().getCompoundOrEmpty(location.toString());
    }

    public void set(Identifier location, CompoundTag data) {
        getData().put(location.toString(), data);
        setDirty();
    }

    // -- Save / Load

    private static final SavedDataType<CustomMailSavedData> TYPE =
          new SavedDataType<>("envelope_mail_data", CustomMailSavedData::new, CODEC, DataFixTypes.LEVEL);

    public static CustomMailSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}

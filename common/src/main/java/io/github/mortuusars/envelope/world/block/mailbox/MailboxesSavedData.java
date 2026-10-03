package io.github.mortuusars.envelope.world.block.mailbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.world.mail.address.type.BlockAddress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MailboxesSavedData extends SavedData {
    public static final Codec<MailboxesSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          Codec.unboundedMap(BlockAddress.STRING_CODEC, RegisteredMailbox.CODEC)
                .optionalFieldOf("mailboxes", Collections.emptyMap()).forGetter(MailboxesSavedData::getMailboxes)
    ).apply(instance, MailboxesSavedData::new));

    private final HashMap<BlockAddress, RegisteredMailbox> mailboxes;

    protected MailboxesSavedData(Map<BlockAddress, RegisteredMailbox> mailboxes) {
        this.mailboxes = new HashMap<>(mailboxes); // Make sure it's mutable
    }

    protected MailboxesSavedData() {
        this(new HashMap<>());
    }

    public HashMap<BlockAddress, RegisteredMailbox> getMailboxes() {
        return mailboxes;
    }

    // -- Save / Load

    public static MailboxesSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private static final SavedDataType<MailboxesSavedData> TYPE =
          new SavedDataType<>("envelope_mailboxes", MailboxesSavedData::new, CODEC, DataFixTypes.LEVEL);
}
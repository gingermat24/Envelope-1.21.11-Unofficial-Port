package io.github.mortuusars.envelope.world.item.component.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryLog;
import io.github.mortuusars.envelope.world.mail.address.Address;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public record DeliveryInfo(Optional<Address> sender, DeliveryLog log, boolean isReturned) {
    public static final Codec<DeliveryInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
          Address.CODEC.optionalFieldOf("sender").forGetter(DeliveryInfo::sender),
          DeliveryLog.CODEC.optionalFieldOf("log", DeliveryLog.EMPTY).forGetter(DeliveryInfo::log),
          Codec.BOOL.optionalFieldOf("returned", false).forGetter(DeliveryInfo::isReturned)
    ).apply(i, DeliveryInfo::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeliveryInfo> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.optional(Address.STREAM_CODEC), DeliveryInfo::sender,
          DeliveryLog.STREAM_CODEC, DeliveryInfo::log,
          ByteBufCodecs.BOOL, DeliveryInfo::isReturned,
          DeliveryInfo::new
    );

    public static final DeliveryInfo EMPTY = new DeliveryInfo(Optional.empty(), DeliveryLog.EMPTY, false);

    public static DeliveryInfo of(ItemStack stack) {
        DeliveryInfo info = stack.get(Envelope.DataComponents.MAIL_DELIVERY_INFO);
        if (info != null) {
            return info;
        }

        return fromLegacy(
              stack.get(Envelope.DataComponents.MAIL_SENDER),
              stack.get(Envelope.DataComponents.MAIL_DELIVERY_LOG),
              stack.has(Envelope.DataComponents.MAIL_RETURNED));
    }

    public static DeliveryInfo fromLegacy(@Nullable Address sender, @Nullable DeliveryLog log, boolean returned) {
        return new DeliveryInfo(Optional.ofNullable(sender), log != null ? log : DeliveryLog.EMPTY, returned);
    }

    public Mutable mutable() {
        return new Mutable(this);
    }

    public static class Mutable {
        private @Nullable Address sender;
        private DeliveryLog log;
        private boolean returned;

        public Mutable(DeliveryInfo info) {
            sender = info.sender.orElse(null);
            log = info.log;
            returned = info.isReturned;
        }

        public Mutable sender(@Nullable Address sender) {
            this.sender = sender;
            return this;
        }

        public Mutable setLog(DeliveryLog log) {
            this.log = log;
            return this;
        }

        public Mutable updateLog(Function<DeliveryLog, DeliveryLog> updater) {
            this.log = updater.apply(log);
            return this;
        }

        public Mutable returned(boolean returned) {
            this.returned = returned;
            return this;
        }

        public DeliveryInfo immutable() {
            return new DeliveryInfo(Optional.ofNullable(sender), log, returned);
        }

        public ItemStack immutableApplyTo(ItemStack stack) {
            stack.set(Envelope.DataComponents.MAIL_DELIVERY_INFO, immutable());
            stack.remove(Envelope.DataComponents.MAIL_SENDER);
            stack.remove(Envelope.DataComponents.MAIL_DELIVERY_LOG);
            stack.remove(Envelope.DataComponents.MAIL_RETURNED);
            return stack;
        }
    }
}

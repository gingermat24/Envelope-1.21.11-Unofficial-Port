package io.github.mortuusars.envelope.world.item.component.seal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Optional;
import java.util.UUID;

public record Seal(Holder<SealMaterial> material, Holder<SealSymbol> impression, Component signature,
                   Optional<UUID> playerUuid) implements TooltipComponent {
    public Seal(Holder<SealMaterial> material, Holder<SealSymbol> impression, Component signature) {
        this(material, impression, signature, Optional.empty());
    }

    public static final Codec<Seal> CODEC = RecordCodecBuilder.create(i -> i.group(
          SealMaterial.CODEC.fieldOf("material").forGetter(Seal::material),
          SealSymbol.CODEC.fieldOf("impression").forGetter(Seal::impression),
          ComponentSerialization.CODEC.optionalFieldOf("signature", CommonComponents.EMPTY).forGetter(Seal::signature),
          UUIDUtil.LENIENT_CODEC.optionalFieldOf("player_id").forGetter(Seal::playerUuid)
    ).apply(i, Seal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Seal> STREAM_CODEC = StreamCodec.composite(
          SealMaterial.STREAM_CODEC, Seal::material,
          SealSymbol.STREAM_CODEC, Seal::impression,
          ComponentSerialization.STREAM_CODEC, Seal::signature,
          ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), Seal::playerUuid,
          Seal::new
    );
}
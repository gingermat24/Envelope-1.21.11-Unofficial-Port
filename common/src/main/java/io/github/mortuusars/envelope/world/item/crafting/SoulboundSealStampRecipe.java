package io.github.mortuusars.envelope.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.SealStampItem;
import io.github.mortuusars.envelope.world.item.SoulboundSealStampItem;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import io.github.mortuusars.envelope.world.item.component.seal.SealSymbol;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulboundSealStampRecipe extends CustomRecipe {
    public SoulboundSealStampRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int stampCount = 0;
        int shardCount = 0;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof SealStampItem && !(stack.getItem() instanceof SoulboundSealStampItem)) {
                if (++stampCount > 1) {
                    return false;
                }
            } else if (stack.is(net.minecraft.world.item.Items.ECHO_SHARD)) {
                if (++shardCount > 1) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return stampCount == 1 && shardCount == 1;
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof SealStampItem && !(stack.getItem() instanceof SoulboundSealStampItem)) {
                ItemStack result = new ItemStack(Envelope.Items.SOULBOUND_SEAL_STAMP.get());
                result.set(Envelope.DataComponents.SEAL_STAMP_MATERIAL,
                      SealMaterial.getOrThrow(registries, SealMaterial.SCULK));

                @Nullable Holder<SealMaterial> originalMaterial =
                      stack.get(Envelope.DataComponents.SEAL_STAMP_MATERIAL);
                if (originalMaterial != null) {
                    result.set(Envelope.DataComponents.SOULBOUND_STAMP_ORIGINAL_MATERIAL, originalMaterial);
                }

                @Nullable Holder<SealSymbol> die = stack.get(Envelope.DataComponents.SEAL_STAMP_DIE);
                if (die == null) {
                    die = stack.get(Envelope.DataComponents.SEAL_STAMP_IMPRESSION);
                }
                if (die != null) {
                    result.set(Envelope.DataComponents.SEAL_STAMP_DIE, die);
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Envelope.RecipeSerializers.SOULBOUND_SEAL_STAMP.get();
    }

    public static class Serializer implements RecipeSerializer<SoulboundSealStampRecipe> {
        private static final MapCodec<SoulboundSealStampRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
              instance.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.EQUIPMENT)
                    .forGetter(SoulboundSealStampRecipe::category))
                    .apply(instance, SoulboundSealStampRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SoulboundSealStampRecipe> STREAM_CODEC =
              StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, SoulboundSealStampRecipe::category,
                    SoulboundSealStampRecipe::new);

        @Override
        public @NotNull MapCodec<SoulboundSealStampRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SoulboundSealStampRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

package io.github.mortuusars.envelope.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.SealStampItem;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SealStampDyeRecipe extends CustomRecipe {
    private final Ingredient material;
    private final Holder<SealMaterial> resultMaterial;

    public SealStampDyeRecipe(CraftingBookCategory category, Ingredient material, Holder<SealMaterial> resultMaterial) {
        super(category);
        this.material = material;
        this.resultMaterial = resultMaterial;
    }

    public boolean matches(CraftingInput input, Level level) {
        int stampCount = 0;
        int materialCount = 0;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() instanceof SealStampItem) {
                if (++stampCount > 1) {
                    return false;
                }
            } else if (material.test(stack)) {
                if (++materialCount > 1) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return stampCount == 1 && materialCount == 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof SealStampItem) {
                ItemStack result = stack.copyWithCount(1);
                result.set(Envelope.DataComponents.SEAL_STAMP_MATERIAL, resultMaterial);
                return result;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Envelope.RecipeSerializers.SEAL_STAMP_DYE.get();
    }

    private Ingredient material() {
        return material;
    }

    private Holder<SealMaterial> resultMaterial() {
        return resultMaterial;
    }

    public static class Serializer implements RecipeSerializer<SealStampDyeRecipe> {
        private static final MapCodec<SealStampDyeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
              CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.EQUIPMENT)
                    .forGetter(SealStampDyeRecipe::category),
              Ingredient.CODEC.fieldOf("material").forGetter(SealStampDyeRecipe::material),
              SealMaterial.CODEC.fieldOf("result_material").forGetter(SealStampDyeRecipe::resultMaterial)
        ).apply(instance, SealStampDyeRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SealStampDyeRecipe> STREAM_CODEC = StreamCodec.composite(
              CraftingBookCategory.STREAM_CODEC, SealStampDyeRecipe::category,
              Ingredient.CONTENTS_STREAM_CODEC, SealStampDyeRecipe::material,
              SealMaterial.STREAM_CODEC, SealStampDyeRecipe::resultMaterial,
              SealStampDyeRecipe::new
        );

        @Override
        public @NotNull MapCodec<SealStampDyeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SealStampDyeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

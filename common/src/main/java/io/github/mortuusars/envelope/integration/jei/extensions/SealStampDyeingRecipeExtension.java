package io.github.mortuusars.envelope.integration.jei.extensions;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import io.github.mortuusars.envelope.world.item.crafting.SealStampDyeRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;

public class SealStampDyeingRecipeExtension implements ICraftingCategoryExtension<SealStampDyeRecipe> {
    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<SealStampDyeRecipe> recipeHolder) {
        return List.of();
    }

    @Override
    public void setRecipe(RecipeHolder<SealStampDyeRecipe> holder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<ItemStack> stamps = List.of(new ItemStack(Envelope.Items.SEAL_STAMP.get()));
        List<ItemStack> dyes = new ArrayList<>();
        List<ItemStack> results = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            ResourceKey<SealMaterial> materialKey = ResourceKey.create(
                  Envelope.Registries.SEAL_MATERIAL, Envelope.resource(color.getName()));
            ItemStack result = new ItemStack(Envelope.Items.SEAL_STAMP.get());
            result.set(Envelope.DataComponents.SEAL_STAMP_MATERIAL,
                  SealMaterial.getOrThrow(Minecrft.registryAccess(), materialKey));
            dyes.add(new ItemStack(DyeItem.byColor(color)));
            results.add(result);
        }

        List<IRecipeSlotBuilder> inputSlots = craftingGridHelper.createAndSetInputs(
              builder, VanillaTypes.ITEM_STACK, List.of(stamps, dyes), 0, 0);
        IRecipeSlotBuilder outputSlot = craftingGridHelper.createAndSetOutputs(builder, results);
        builder.createFocusLink(inputSlots.get(1), outputSlot);
    }
}

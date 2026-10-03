package io.github.mortuusars.envelope.world.item.crafting.mail;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.PackageContents;
import io.github.mortuusars.envelope.world.mail.address.type.ServiceAddress;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface MailRecipe extends Recipe<MailRecipeInput> {
    ServiceAddress getAddress();

    @Override
    default @NotNull RecipeType<? extends Recipe<MailRecipeInput>> getType() {
        return Envelope.RecipeTypes.MAILING.get();
    }

    default boolean isSpecial() {
        return true;
    }

    /**
     * Whether the crafting should only be done one time per delivery.<br>
     * Some recipes (and their side effects) could get out of control when multiple is allowed (like letter broadcasting).
     */
    default boolean isOneCraftPerDelivery() {
        return false;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.create(getIngredients());
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    NonNullList<Ingredient> getIngredients();

    default float getExperience() {
        return 0f;
    }

    default int getExperiencePoints() {
        float xp = getExperience();
        int points = Mth.floor(xp);
        float remainder = Mth.frac(xp);
        if (remainder != 0.0F && Math.random() < remainder) {
            points++;
        }
        return points;
    }

    // --

    default boolean matches(MailRecipeInput input, Level level) {
        if (input.ingredientCount() != getIngredients().size()) {
            return false;
        }
        return input.size() == 1 && getIngredients().size() == 1
              ? getIngredients().getFirst().test(input.getItem(0))
              : input.stackedContents().tryPick(getIngredients().stream()
                    .<StackedContents.IngredientInfo<ItemStack>>map(ingredient -> ingredient::test)
                    .toList(), 1, null);
    }

    /**
     * Regular result assemble method. Does not mutate world state. Can be called as many times as needed.
     */
    default @NotNull ItemStack assemble(MailRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    ItemStack getResultItem(HolderLookup.Provider registries);

    /**
     * Special assemble method, that allows performing extra actions, that could potentially mutate the world state.<br>
     * - This method should be called only once per craft, when the crafting is guaranteed.<br>
     * - Should return the same item "shape" (type, count), as {@link MailRecipe#assemble(MailRecipeInput, HolderLookup.Provider)},
     * to make sure the crafting works properly. Components can be different.
     */
    default @NotNull ItemStack assembleWithSideEffects(MailRecipeInput input, HolderLookup.Provider registries) {
        return assemble(input, registries);
    }
}

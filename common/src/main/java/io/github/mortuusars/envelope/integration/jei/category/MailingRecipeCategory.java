package io.github.mortuusars.envelope.integration.jei.category;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.integration.jei.EnvelopeJeiPlugin;
import io.github.mortuusars.envelope.integration.jei.EnvelopeJeiRecipeTypes;
import io.github.mortuusars.envelope.world.item.Unsealable;
import io.github.mortuusars.envelope.world.item.component.PackageContents;
import io.github.mortuusars.envelope.world.item.crafting.mail.MailRecipe;
import io.github.mortuusars.envelope.world.mail.address.type.ServiceAddress;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class MailingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MailRecipe>> {
    private final IDrawable background;

    public MailingRecipeCategory(IJeiHelpers helper) {
        super(EnvelopeJeiRecipeTypes.MAILING_RECIPE_TYPE,
              Component.translatable("envelope.jei.category.mailing"),
              helper.getGuiHelper().createDrawableItemLike(Envelope.Items.PACKAGE.get()), 146, 74);
        background = helper.getGuiHelper().createDrawable(
              Envelope.resource("textures/gui/jei/category_mailing.png"), 0, 0, getWidth(), getHeight());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MailRecipe> recipeHolder, IFocusGroup focuses) {
        MailRecipe recipe = recipeHolder.value();

        builder.addInvisibleIngredients(RecipeIngredientRole.CRAFTING_STATION)
              .addItemLike(Envelope.Items.PAPER_BOX.get())
              .addItemLike(Envelope.Items.PACKAGE.get());

        if (Config.Client.JEI_SERVICE_ADDRESS_INGREDIENT.get()) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                  .addIngredient(EnvelopeJeiPlugin.SERVICE_ADDRESS_INGREDIENT, recipe.getAddress());
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                  .addIngredient(EnvelopeJeiPlugin.SERVICE_ADDRESS_INGREDIENT, recipe.getAddress());
        }

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 3; column++) {
                int index = column + row * 3;

                int xPos = 18;
                int yPos = 24;

                if (index >= recipe.getIngredients().size()) continue;

                builder.addInputSlot(xPos + column * 18, yPos + row * 18)
                      .addIngredients(recipe.getIngredients().get(index));
            }
        }

        builder.addOutputSlot(122, 33)
              .addItemStack(recipe.getResultItem(Minecrft.registryAccess()));

        ItemStack resultItem = recipe.getResultItem(Minecrft.registryAccess());
        if (!(resultItem.getItem() instanceof Unsealable)) { // If not sealed
            PackageContents resultContents = PackageContents.of(resultItem);
            if (!resultContents.isEmpty()) {
                // Makes contents "known" to jei usages lookup:
                builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                      .addItemStacks(resultContents.getItems());
            }
        }

        builder.setShapeless(getWidth() - 9, getHeight() - 9);
    }

    @Override
    public void draw(RecipeHolder<MailRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<MailRecipe> recipeHolder, IFocusGroup focuses) {
        builder.addWidget(new ServiceAddressRecipeWidget(
              new ScreenRectangle(0, 0, getWidth(), 12), recipeHolder.value().getAddress()));
        if (Config.Client.JEI_SERVICE_ADDRESS_INGREDIENT.get()) {
            builder.addGuiEventListener(new AddressUsesClickListener(recipeHolder.value().getAddress(), getWidth(), 12));
        }

        builder.addRecipeArrow().setPosition(90, 32);
    }

    private record AddressUsesClickListener(ServiceAddress address, int width, int height)
          implements IJeiGuiEventListener {
        @Override
        public ScreenRectangle getArea() {
            return new ScreenRectangle(0, 0, width, height);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0 || mouseX < 0 || mouseX >= width || mouseY < 0 || mouseY >= height) {
                return false;
            }
            return EnvelopeJeiPlugin.showAddressUsages(address);
        }
    }
}
package io.github.mortuusars.envelope.integration.jei.category;

import io.github.mortuusars.envelope.Envelope;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class MailingRecipeInfoWidget implements IRecipeWidget {
    private static final int ICON_SIZE = 8;
    private static final int TEXTURE_SIZE = 8;
    private static final Identifier TEXTURE = Envelope.resource("textures/gui/jei/info_icon.png");

    private final ScreenRectangle area;
    private final Component info;

    public MailingRecipeInfoWidget(int x, int y, Component info) {
        this.area = new ScreenRectangle(x, y, ICON_SIZE, ICON_SIZE);
        this.info = info;
    }

    @Override
    public @NotNull ScreenPosition getPosition() {
        return area.position();
    }

    @Override
    public ScreenRectangle getScreenRectangle() {
        return area;
    }

    @Override
    public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
              0, 0, 0, 0, ICON_SIZE, ICON_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        if (mouseX >= 0 && mouseX < ICON_SIZE && mouseY >= 0 && mouseY < ICON_SIZE) {
            tooltip.add(Component.translatable("envelope.jei.info"));
            tooltip.add(info.copy().withStyle(ChatFormatting.GRAY));
        }
    }
}

package io.github.mortuusars.envelope.integration.jei.category;

import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.integration.jei.EnvelopeJeiPlugin;
import io.github.mortuusars.envelope.world.mail.address.type.ServiceAddress;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ServiceAddressRecipeWidget implements IRecipeWidget {
    private final ScreenRectangle area;
    private final ServiceAddress address;
    private final Font font;
    private final Component text;
    private final int textWidth;

    public ServiceAddressRecipeWidget(ScreenRectangle area, ServiceAddress address) {
        this.area = area;
        this.address = address;
        this.font = Minecrft.get().font;
        this.text = address.format().withIcon().toComponent();
        this.textWidth = font.width(text);
    }

    @Override
    public @NotNull ScreenPosition getPosition() {
        return area.position();
    }

    @Override
    public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = area.width() / 2 - textWidth / 2;
        int y = 0;
        int color = isHovering(mouseX, mouseY) ? 0xFFFFFFFF : 0xFF808080;
        guiGraphics.drawString(font, text, x, y, color, false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        if (isHovering(mouseX, mouseY)) {
            EnvelopeJeiPlugin.addServiceAddressTooltip(tooltip, address);
        }
    }

    private boolean isHovering(double mouseX, double mouseY) {
        double textX = area.width() / 2.0 - textWidth / 2.0;
        return mouseX >= textX && mouseX <= textX + textWidth
              && mouseY >= 0 && mouseY <= area.height();
    }
}

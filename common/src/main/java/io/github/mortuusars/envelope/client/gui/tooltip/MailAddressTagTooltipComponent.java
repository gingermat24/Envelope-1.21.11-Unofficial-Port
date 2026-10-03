package io.github.mortuusars.envelope.client.gui.tooltip;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.util.Colors;
import io.github.mortuusars.envelope.world.mail.address.Address;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MailAddressTagTooltipComponent implements ClientTooltipComponent {
    private static final ItemStack ADDRESS_TAG = new ItemStack(Envelope.Items.ADDRESS_TAG.get());

    private final @NotNull Component component;

    public MailAddressTagTooltipComponent(Address address) {
        component = address.format().withIcon()
              .withIconColor(Colors.ADDRESS_NEUTRAL)
              .withColor(Colors.ADDRESS_NEUTRAL).toComponent();
    }

    @Override
    public int getWidth(Font font) {
//        if (Screen.hasShiftDown()) {
//            return Math.max(19, Math.max(font.width(fullSenderComponent), font.width(fullComponent)));
//        }
        return 19 + font.width(component);
    }

    @Override
    public int getHeight(Font font) {
//        if (Screen.hasShiftDown()) {
//            return 16 + (!isEmpty(fullSenderComponent) ? 10 : 0) + (!isEmpty(fullComponent) ? 10 : 0);
//        }
        return 16;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        guiGraphics.renderFakeItem(ADDRESS_TAG, x - 1, y - 1, 0);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {
        guiGraphics.drawString(font, component, x + 18, y + 3, 0xFFFFFFFF);
    }
}

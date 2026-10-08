package io.github.mortuusars.envelope.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.util.Colors;
import io.github.mortuusars.envelope.world.block.mailbox.MailboxBlockEntity;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryLog;
import io.github.mortuusars.envelope.world.item.component.mail.log.DeliveryRecord;
import io.github.mortuusars.envelope.world.mail.address.Address;
import io.github.mortuusars.envelope.world.mail.address.AddressFormatter;
import io.github.mortuusars.envelope.client.gui.Sprites;
import io.github.mortuusars.envelope.client.util.Minecrft;
import io.github.mortuusars.envelope.network.Packets;
import io.github.mortuusars.envelope.network.packet.serverbound.MailboxMenuInboxActionC2SP;
import io.github.mortuusars.envelope.world.inventory.MailboxMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.*;

public class MailboxScreen extends AbstractContainerScreen<MailboxMenu> {
    public static final Identifier TEXTURE = Envelope.resource("textures/gui/mailbox.png");

    public static final WidgetSprites ADDRESS_BUTTON_SPRITES = Sprites.normalAndHighlighted(Envelope.resource("mailbox/address_button"));
    public static final WidgetSprites ADDRESS_ATTENTION_BUTTON_SPRITES = Sprites.normalAndHighlighted(Envelope.resource("mailbox/address_attention_button"), Envelope.resource("mailbox/address_button_highlighted"));
    public static final WidgetSprites ADDRESS_DEFAULT_BUTTON_SPRITES = Sprites.normalAndHighlighted(Envelope.resource("mailbox/address_default_button"));

    public static final WidgetSprites REGULAR_MAIL_BUTTON_SPRITES = Sprites.threeStates(Envelope.resource("mailbox/mail_button"));

    public static final WidgetSprites MAIL_ICON_SPRITES = Sprites.normalAndHighlighted(Envelope.resource("mailbox/mail_icon"));
    public static final WidgetSprites MAIL_ICON_RETURNED_SPRITES = Sprites.normalAndHighlighted(Envelope.resource("mailbox/mail_icon_returned"));
    public static final Identifier SPIDER_WEB_SPRITE = Envelope.resource("mailbox/spider_web");
    public static final Identifier SPIDER_SPRITE = Envelope.resource("mailbox/spider");

    public static final WidgetSprites NEW_MAIL_INDICATOR_SPRITES = Sprites.normalOnly(Envelope.resource("mailbox/new_mail_indicator"));

    protected static final int SCROLL_THUMB_TOP_HEIGHT = 3;
    protected static final int SCROLL_THUMB_MID_HEIGHT = 2;
    protected static final int SCROLL_THUMB_BOT_HEIGHT = 2;
    protected static final int SCROLL_THUMB_Y_OFFSET = SCROLL_THUMB_TOP_HEIGHT + SCROLL_THUMB_MID_HEIGHT + SCROLL_THUMB_BOT_HEIGHT;

    protected static final int MAX_INBOX_MAIL_BUTTONS = 8;

    protected Component inboxLabel = Component.translatable("gui.envelope.mailbox.inbox");
    protected Component sendLabel = Component.translatable("gui.envelope.mailbox.send");

    @Nullable
    protected ItemStack hoveredMail;

    protected ImageButton addressButton;
    protected ImageButton addressAttentionButton;
    protected ImageButton addressDefaultButton;
    protected ImageButton newMailButton;

    protected Rect2i mailArea = new Rect2i(0, 0, 0, 0);
    protected Rect2i scrollBarArea = new Rect2i(0, 0, 0, 0);
    protected Rect2i scrollThumb = new Rect2i(0, 0, 0, 0);
    protected int scroll = 0;
    protected int scrollAtDragStart = 0;
    protected boolean isDraggingScrollbar = false;
    protected boolean hadMail = false;
    protected double dragDelta = 0;

    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    // --

    @Override
    protected void init() {
        imageWidth = 308;
        imageHeight = 170;
        titleLabelX = Math.max(17, (imageWidth / 2) - (font.width(title) / 2) + 5);
        titleLabelY = -10;
        inventoryLabelX = 140;
        inventoryLabelY = imageHeight - 94;
        super.init();
        mailArea = new Rect2i(leftPos + 8, topPos + 18, 117, 144);
        scrollBarArea = new Rect2i(leftPos + 128, topPos + 18, 6, 144);

        addressButton = new ImageButton(leftPos + titleLabelX - 11, topPos - 11, 10, 10,
              ADDRESS_BUTTON_SPRITES,
              button -> setAsDefaultAddress(),
              Component.translatable("gui.envelope.mailbox.address"));
        addressButton.setTooltip(Tooltip.create(Component.translatable("gui.envelope.mailbox.address")
              .append("\n")
              .append(Component.translatable("gui.envelope.mailbox.address.tooltip"))));
        addRenderableWidget(addressButton);

        addressAttentionButton = new ImageButton(leftPos + titleLabelX - 11, topPos - 11, 10, 10,
              ADDRESS_ATTENTION_BUTTON_SPRITES,
              button -> setAsDefaultAddress(),
              Component.translatable("gui.envelope.mailbox.address"));
        addressAttentionButton.setTooltip(Tooltip.create(Component.translatable("gui.envelope.mailbox.address")
              .append("\n")
              .append(Component.translatable("gui.envelope.mailbox.address.tooltip"))));
        addRenderableWidget(addressAttentionButton);

        addressDefaultButton = new ImageButton(leftPos + titleLabelX - 11, topPos - 11, 10, 10, ADDRESS_DEFAULT_BUTTON_SPRITES, btn -> {
        });
        addressDefaultButton.setTooltip(Tooltip.create(Component.translatable("gui.envelope.mailbox.address.default")
              .append("\n")
              .append(Component.translatable("gui.envelope.mailbox.address.default.tooltip"))));
        addressDefaultButton.active = false;
        addRenderableWidget(addressDefaultButton);

        newMailButton = new ImageButton(leftPos + 7, topPos + 6, 8, 8,
              NEW_MAIL_INDICATOR_SPRITES,
              button -> {
                  refreshMail();
                  scrollTo(0);
              });
        newMailButton.setTooltip(Tooltip.create(Component.translatable("gui.envelope.mailbox.mail.tooltip.new_mail")
              .append("\n")
              .append(Component.translatable("gui.envelope.mailbox.mail.tooltip.new_mail.click_to_refresh"))));
        addRenderableWidget(newMailButton);

        updateButtons();
    }

    protected void setAsDefaultAddress() {
        Minecrft.gameMode().handleInventoryButtonClick(getMenu().containerId, MailboxMenu.ADDRESS_BUTTON_ID);
    }

    protected void refreshMail() {
        Minecrft.gameMode().handleInventoryButtonClick(getMenu().containerId, MailboxMenu.REFRESH_MAIL_BUTTON_ID);
    }

    protected void updateScrollThumb() {
        int minSize = SCROLL_THUMB_TOP_HEIGHT + SCROLL_THUMB_MID_HEIGHT + SCROLL_THUMB_BOT_HEIGHT;

        int totalButtons = getMenu().getMail().size();
        float ratio = MAX_INBOX_MAIL_BUTTONS / (float) Math.max(totalButtons, 1);
        int size = Mth.clamp(Mth.ceil(scrollBarArea.getHeight() * ratio), minSize, scrollBarArea.getHeight());
        int midSize = size - SCROLL_THUMB_TOP_HEIGHT - SCROLL_THUMB_BOT_HEIGHT;
        int correctedMidSize = Math.max(midSize - (midSize % SCROLL_THUMB_MID_HEIGHT), SCROLL_THUMB_MID_HEIGHT);
        size = SCROLL_THUMB_TOP_HEIGHT + correctedMidSize + SCROLL_THUMB_BOT_HEIGHT;

        float topRowPos = (float) scroll / Math.max(1, totalButtons - MAX_INBOX_MAIL_BUTTONS);
        int pos = (int) Mth.map(topRowPos, 0f, 1f, 0f, scrollBarArea.getHeight() - size);

        scrollThumb = new Rect2i(scrollBarArea.getX(), scrollBarArea.getY() + pos, scrollBarArea.getWidth(), size);
    }

    protected void updateButtons() {
        addressButton.visible = !getMenu().isDefaultAddress() && getMenu().hasDefaultAddress();
        addressAttentionButton.visible = !getMenu().hasDefaultAddress();
        addressDefaultButton.visible = getMenu().isDefaultAddress();
        newMailButton.visible = getMenu().hasNewMail();
    }

    // --

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        super.renderSlot(guiGraphics, slot, mouseX, mouseY);

        // Extend slot highlight for mail slot to cover whole rectangle (mail slot is slightly bigger):

        if (slot.isActive() && slot.getContainerSlot() == MailboxBlockEntity.SLOT_MAIL
              && isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
            int highlight = 0x80FFFFFF;
            guiGraphics.fill(slot.x - 1, slot.y - 1, slot.x + 17, slot.y, highlight);
            guiGraphics.fill(slot.x - 1, slot.y, slot.x, slot.y + 16, highlight);
            guiGraphics.fill(slot.x + 16, slot.y, slot.x + 17, slot.y + 16, highlight);
            guiGraphics.fill(slot.x - 1, slot.y + 16, slot.x + 17, slot.y + 17, highlight);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        hoveredMail = null;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos,
              0, 0, imageWidth, imageHeight, 512, 256);

        int addressBarX = titleLabelX - 18;
        int addressBarWidth = imageWidth - (addressBarX * 2);
        // Left
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + addressBarX, topPos - 15,
              0, imageHeight, 5, 15, 512, 256);
        // Middle
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + addressBarX + 5, topPos - 15,
              5, imageHeight, addressBarWidth - 10, 15, 512, 256);
        // Right
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + addressBarX + addressBarWidth - 5, topPos - 15,
              303, imageHeight, 5, 15, 512, 256);

        List<ItemStack> mail = getMenu().getMail();

        if (!mail.isEmpty()) {
            hadMail = true;
            scroll = Math.clamp(scroll, 0, Math.max(0, mail.size() - MAX_INBOX_MAIL_BUTTONS));

            for (int i = 0; i < Math.min(mail.size(), MAX_INBOX_MAIL_BUTTONS); i++) {
                int index = i + scroll;

                ItemStack item = mail.get(index);
                int x = 8;
                int y = 18 + 18 * i;
                boolean isHovering = isHovering(x + 1, y + 1, 115, 16, mouseX, mouseY);
                if (isHovering) {
                    hoveredMail = item;
                }
                renderMailButton(guiGraphics, partialTick, mouseX, mouseY, item, leftPos + x, topPos + y);
            }
        } else {
            if (!hadMail && MonthDay.from(LocalDate.now()).equals(MonthDay.of(10, 31))) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPIDER_WEB_SPRITE, leftPos + 8, topPos + 18, 117, 144);
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPIDER_SPRITE, leftPos + 22, topPos + 38, 11, 11);
            }

            if (this.hashCode() % 20 == 0) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 59, topPos + 92,
                      348, 0, 17, 9, 512, 256);
            }
        }

        Slot foodSlot = getMenu().getSlot(MailboxBlockEntity.SLOT_FOOD);
        if (!foodSlot.hasItem()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + foodSlot.x, topPos + foodSlot.y,
                  314, 0, 16, 16, 512, 256);
        }
        Slot mailSlot = getMenu().getSlot(MailboxBlockEntity.SLOT_MAIL);
        if (!mailSlot.hasItem()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + mailSlot.x - 1, topPos + mailSlot.y - 1,
                  330, 0, 18, 18, 512, 256);
        }

        renderScrollBar(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0xFF404040, false);

        int inboxLabelX = 68 - font.width(inboxLabel) / 2;
        guiGraphics.drawString(font, inboxLabel, inboxLabelX, 6, 0xFF404040, false);

        if (getMenu().getMail().isEmpty()) {
            Component empty = Component.translatable("gui.envelope.mailbox.empty");
            int emptyLabelX = 68 - font.width(empty) / 2;
            guiGraphics.drawString(font, empty, emptyLabelX, 80, 0x606060, false);
        }

        int sendLabelX = 220 - font.width(sendLabel) / 2;
        guiGraphics.drawString(font, sendLabel, sendLabelX, 6, 0xFF404040, false);

        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFF404040, false);
    }

    protected void renderMailButton(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, ItemStack mail, int x, int y) {
        boolean isHovered = hoveredMail == mail;

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, REGULAR_MAIL_BUTTON_SPRITES.get(true, isHovered), x, y, 18, 18);

        guiGraphics.renderItem(mail, x + 2, y + 1);

        MailIcon icon = MailIcon.create(mail);
        Identifier iconSprite = isHovered ? icon.sprites().enabledFocused() : icon.sprites().enabled();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, iconSprite, x + 23, y + 4, 10, 10);

        if (!icon.icon().isEmpty()) {
            guiGraphics.drawString(font, icon.icon(), x + 26, y + 5, 0xFFC6B38F, false);
        }

        String sender = getDisplayedSender(mail).getString();
        if (font.width(sender) > 76) {
            sender = font.plainSubstrByWidth(sender, 72) + "...";
        }
        guiGraphics.drawString(font, sender, x + 36, y + 5, 0xFF886447, false);
    }

    protected void renderScrollBar(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        updateScrollThumb();

        int state = 0;
        if (!canScroll()) {
            state = 2;
        } else if (isDraggingScrollbar || isMouseOver(scrollThumb, mouseX, mouseY)) {
            state = 1;
        }

        // Top
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollThumb.getX(), scrollThumb.getY(),
              308, state * SCROLL_THUMB_Y_OFFSET, scrollThumb.getWidth(), SCROLL_THUMB_TOP_HEIGHT, 512, 256);

        // Middle
        int middlePartsCount = (scrollThumb.getHeight() - SCROLL_THUMB_TOP_HEIGHT - SCROLL_THUMB_BOT_HEIGHT) / SCROLL_THUMB_MID_HEIGHT;

        for (int i = 0; i < middlePartsCount; i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollThumb.getX(),
                  scrollThumb.getY() + SCROLL_THUMB_TOP_HEIGHT + i * SCROLL_THUMB_MID_HEIGHT,
                  308, state * SCROLL_THUMB_Y_OFFSET + SCROLL_THUMB_TOP_HEIGHT,
                  scrollThumb.getWidth(), SCROLL_THUMB_MID_HEIGHT, 512, 256);
        }

        // Bottom
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollThumb.getX(), scrollThumb.getY() + SCROLL_THUMB_TOP_HEIGHT + (middlePartsCount * SCROLL_THUMB_MID_HEIGHT),
              308, SCROLL_THUMB_TOP_HEIGHT + SCROLL_THUMB_MID_HEIGHT + state * SCROLL_THUMB_Y_OFFSET,
              scrollThumb.getWidth(), SCROLL_THUMB_BOT_HEIGHT, 512, 256);

        if (!canScroll()) {
            // Special case to make scroll thumb fill remaining gap in the bottom
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollThumb.getX(), scrollThumb.getY() + SCROLL_THUMB_TOP_HEIGHT + (middlePartsCount * SCROLL_THUMB_MID_HEIGHT) + 1,
                  308, SCROLL_THUMB_TOP_HEIGHT + SCROLL_THUMB_MID_HEIGHT + state * SCROLL_THUMB_Y_OFFSET,
                  scrollThumb.getWidth(), SCROLL_THUMB_BOT_HEIGHT, 512, 256);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if (hoveredMail != null) {
            renderMailTooltip(guiGraphics, x, y, hoveredMail);
        }
    }

    protected void renderMailTooltip(GuiGraphics guiGraphics, int x, int y, ItemStack hoveredMail) {
        if (x >= leftPos + 8 && x < leftPos + 28) {
            guiGraphics.setTooltipForNextFrame(font, getTooltipFromContainerItem(hoveredMail), hoveredMail.getTooltipImage(), x, y);
            return;
        }

        if (x >= leftPos + 31 && x < leftPos + 41) {
            guiGraphics.setTooltipForNextFrame(font, MailIcon.create(hoveredMail).name(), x, y);
            return;
        }

        List<Component> tooltip = new ArrayList<>();

        // Show full sender address if it doesn't fit on the widget
        Address sender = getDisplayedSender(hoveredMail);
        if (font.width(sender.getString()) > 76) {
            tooltip.add(sender.format()
                  .withIcon()
                  .withIconColor(Colors.ADDRESS_NEUTRAL)
                  .withColor(ChatFormatting.WHITE)
                  .toComponent());
        }

        DeliveryLog deliveryLog = Mail.getLog(hoveredMail);
        if (!deliveryLog.isEmpty()) {
            tooltip.add(Component.translatable("gui.envelope.delivery_log"));
            for (DeliveryRecord record : deliveryLog.records()) {
                tooltip.add(record.getDisplayComponent());
            }
        }

        if (!tooltip.isEmpty()) {
            guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), x, y);
        }
    }

    // -- Scroll

    public boolean canScroll() {
        return getMenu().getMail().size() > MAX_INBOX_MAIL_BUTTONS;
    }

    public void scroll(int amount) {
        scrollTo(scroll + amount);
    }

    public void scrollTo(int buttonIndex) {
        int maxScrollWhenAtEnd = Math.max(0, getMenu().getMail().size() - MAX_INBOX_MAIL_BUTTONS);
        scroll = Mth.clamp(buttonIndex, 0, maxScrollWhenAtEnd);
    }

    // -- Input


    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == InputConstants.KEY_HOME) {
            scroll(Integer.MIN_VALUE);
            return true;
        }
        if (keyCode == InputConstants.KEY_END) {
            scroll(Integer.MAX_VALUE);
            return true;
        }
        if (keyCode == InputConstants.KEY_UP) {
            scroll(-1);
            return true;
        }
        if (keyCode == InputConstants.KEY_DOWN) {
            scroll(1);
            return true;
        }


        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == InputConstants.MOUSE_BUTTON_LEFT && hoveredMail != null) {
            int index = getMenu().getMail().indexOf(hoveredMail);
            if (index == -1) return false;

            MailboxMenu.MailAction action = MailboxMenu.MailAction.PICK_UP;
            if (Minecraft.getInstance().hasShiftDown()) {
                if (Minecraft.getInstance().hasControlDown()) {
                    action = MailboxMenu.MailAction.MOVE_ALL_TO_INVENTORY;
                } else {
                    action = MailboxMenu.MailAction.MOVE_TO_INVENTORY;
                }
            }

            if (getMenu().doMailAction(Minecrft.player(), index, action)) {
                Minecrft.player().playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1, 1);
                Packets.sendToServer(new MailboxMenuInboxActionC2SP(index, action));
            }
        }

        if (canScroll()) {
            if (isMouseOver(scrollThumb, mouseX, mouseY)) {
                setDragging(true);
                isDraggingScrollbar = true;
                dragDelta = 0;
                scrollAtDragStart = scroll;
                return true;
            } else if (isMouseOver(scrollBarArea, mouseX, mouseY)) {
                int direction = mouseY < scrollThumb.getY() ? -1 : 1;
                scroll(MAX_INBOX_MAIL_BUTTONS * direction);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        isDraggingScrollbar = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!isDraggingScrollbar || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return super.mouseDragged(event, dragX, dragY);
        }

        dragDelta += dragY;

        double threshold = (double) scrollBarArea.getHeight() / Math.max(getMenu().getMail().size(), 1);
        int amount = (int) (dragDelta / threshold);
        if (amount != 0 || scroll != scrollAtDragStart) {
            scrollTo(scrollAtDragStart + amount);
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mailArea, mouseX, mouseY) || isMouseOver(scrollBarArea, mouseX, mouseY)) {
            scroll((int) -scrollY);
            return true;
        }
        return false;
    }

    // --

    protected Address getDisplayedSender(ItemStack mail) {
        return Mail.getSenderOrUnknown(mail);
    }

    public record MailIcon(WidgetSprites sprites, String icon, Component name) {
        public static MailIcon create(ItemStack mail) {
            if (Mail.isReturned(mail)) {
                return new MailIcon(MAIL_ICON_RETURNED_SPRITES, "", Component.translatable("gui.envelope.mail.returned"));
            }

            Address sender = Mail.getSenderOrUnknown(mail);
            return new MailIcon(MAIL_ICON_SPRITES, AddressFormatter.getIcon(sender), sender.getType().translate());
        }
    }

    // --

    protected boolean isMouseOver(Rect2i rect, double mouseX, double mouseY) {
        return mouseX >= rect.getX() && mouseX < rect.getX() + rect.getWidth()
              && mouseY >= rect.getY() && mouseY < rect.getY() + rect.getHeight();
    }
}
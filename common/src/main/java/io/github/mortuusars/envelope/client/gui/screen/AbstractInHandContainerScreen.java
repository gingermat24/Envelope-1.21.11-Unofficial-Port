package io.github.mortuusars.envelope.client.gui.screen;

import io.github.mortuusars.envelope.world.inventory.AbstractInHandContainerMenu;
import io.github.mortuusars.envelope.world.inventory.slot.DisabledSlot;
import io.github.mortuusars.envelope.world.inventory.slot.PickupOnlySlot;
import io.github.mortuusars.envelope.world.inventory.slot.PreviewSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class AbstractInHandContainerScreen<T extends AbstractInHandContainerMenu> extends AbstractContainerScreen<T> {
    private final Identifier texture;

    public AbstractInHandContainerScreen(T menu, Inventory playerInventory, Component title, Identifier texture) {
        super(menu, playerInventory, title);
        this.texture = texture;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = imageHeight - 94;
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void updateButtons() {

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos,
              0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        super.renderSlot(guiGraphics, slot, mouseX, mouseY);
        renderSlotOverlays(guiGraphics, slot);
    }

    protected void renderSlotOverlays(GuiGraphics guiGraphics, Slot slot) {
        if (slot instanceof PreviewSlot) {
            guiGraphics.nextStratum();
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PreviewSlot.OVERLAY_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
        } else if (shouldRenderDisabledOverlayOver(slot)) {
            guiGraphics.nextStratum();
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, DisabledSlot.OVERLAY_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
        }
    }

    protected boolean shouldRenderDisabledOverlayOver(Slot slot) {
        return slot instanceof DisabledSlot
              || slot instanceof PickupOnlySlot && !slot.hasItem()
              || (!slot.hasItem() && !slot.mayPickup(getMenu().getPlayer()));
    }
}

package io.github.mortuusars.envelope.world.item;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.SealLock;
import io.github.mortuusars.envelope.world.item.component.seal.Seal;
import io.github.mortuusars.envelope.world.item.component.seal.SealMaterial;
import io.github.mortuusars.envelope.world.inventory.tooltip.SealDieTooltipComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SoulboundSealStampItem extends SealStampItem {
    public SoulboundSealStampItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new SealDieTooltipComponent(
              getDie(stack),
              Optional.ofNullable(stack.get(Envelope.DataComponents.SEAL_STAMP_MATERIAL)),
              true));
    }

    @Override
    public Seal createSeal(ItemStack stack, Player player) {
        Holder<SealMaterial> sculk = SealMaterial.getOrThrow(player.registryAccess(), SealMaterial.SCULK);
        return new Seal(sculk, getDieOrDefault(stack, player.registryAccess(), player), player.getName(),
              Optional.of(player.getUUID()), Optional.of(SealLock.create(player.getScoreboardName())));
    }

    @Override
    protected Holder<SealMaterial> getStampMaterial(ItemStack stack, Player player) {
        return SealMaterial.getOrThrow(player.registryAccess(), SealMaterial.SCULK);
    }

    @Override
    protected boolean canRecolorExistingSeal(ItemStack stack, Player player) {
        return false;
    }

    @Override
    protected void onSealApplied(ItemStack stampStack, Player player, Seal seal) {
        seal.lock().ifPresent(lock -> lock.lock(player.level()));

        Holder<SealMaterial> originalMaterial =
              stampStack.get(Envelope.DataComponents.SOULBOUND_STAMP_ORIGINAL_MATERIAL);
        Holder<io.github.mortuusars.envelope.world.item.component.seal.SealSymbol> originalDie =
              stampStack.get(Envelope.DataComponents.SEAL_STAMP_DIE);

        stampStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        if (!stampStack.isEmpty()) {
            return;
        }

        ItemStack regularStamp = new ItemStack(Envelope.Items.SEAL_STAMP.get());
        if (originalMaterial != null) {
            regularStamp.set(Envelope.DataComponents.SEAL_STAMP_MATERIAL, originalMaterial);
        }
        if (originalDie != null) {
            regularStamp.set(Envelope.DataComponents.SEAL_STAMP_DIE, originalDie);
        }

        if (!player.getInventory().add(regularStamp)) {
            player.drop(regularStamp, false);
        }
    }
}

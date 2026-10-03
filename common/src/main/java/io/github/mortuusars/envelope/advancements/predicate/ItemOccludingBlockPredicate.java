package io.github.mortuusars.envelope.advancements.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class ItemOccludingBlockPredicate implements DataComponentPredicate {
    public static final ItemOccludingBlockPredicate INSTANCE = new ItemOccludingBlockPredicate();
    public static final Codec<ItemOccludingBlockPredicate> CODEC = MapCodec.unitCodec(INSTANCE);

    private ItemOccludingBlockPredicate() {}

    @Override
    public boolean matches(DataComponentGetter components) {
        // This is a crude way to check for at least somewhat heavy blocks. This is probably the best we can do without having access to level and position.
        return components instanceof ItemStack stack
              && stack.getItem() instanceof BlockItem blockItem
              && blockItem.getBlock().defaultBlockState().canOcclude();
    }
}

package io.github.mortuusars.envelope.advancements.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.item.component.PackageContents;
import net.minecraft.advancements.criterion.CollectionPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.predicates.DataComponentPredicate;

import java.util.Optional;

public record ItemPackagePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements DataComponentPredicate {
    public static final Codec<ItemPackagePredicate> CODEC = RecordCodecBuilder.create(i -> i.group(
                CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ItemPackagePredicate::items))
          .apply(i, ItemPackagePredicate::new)
    );

    @Override
    public boolean matches(DataComponentGetter components) {
        PackageContents contents = components.get(Envelope.DataComponents.PACKAGE_CONTENTS);
        return contents != null && (items.isEmpty() || items.get().test(contents.getItems()));
    }
}

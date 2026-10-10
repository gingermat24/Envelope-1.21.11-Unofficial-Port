package io.github.mortuusars.envelope.world.item;

import net.minecraft.world.level.block.Block;

public class TatteredLetterItem extends LetterItem {
    public TatteredLetterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean isTattered() {
        return true;
    }
}

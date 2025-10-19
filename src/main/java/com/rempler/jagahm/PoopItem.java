package com.rempler.jagahm;

import net.minecraft.item.BoneMealItem;

public class PoopItem extends BoneMealItem {
    public PoopItem() {
        this(new Settings().maxCount(64));
    }

    public PoopItem(Settings settings) {
        super(settings);
    }
}

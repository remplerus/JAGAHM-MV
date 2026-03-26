package com.rempler.jagahm;

import net.minecraft.world.item.BoneMealItem;

public class PoopItem extends BoneMealItem {
    public PoopItem() {
        this(new Properties().stacksTo(64));
    }

    public PoopItem(Properties settings) {
        super(settings);
    }
}

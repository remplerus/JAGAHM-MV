package com.rempler.jagahm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BoneMealItem;

public class PoopItem extends BoneMealItem {
    public PoopItem() {
        //? if >1.21.1 {
        super(new Properties().stacksTo(64).setId(ResourceKey.create(Registries.ITEM, JAGAHM.rl("poop"))));
        //?} else {
        /*super(new Properties().stacksTo(64));*/
        //?}
    }
}
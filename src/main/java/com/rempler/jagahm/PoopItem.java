package com.rempler.jagahm;

//? if >1.16.5 {
import net.minecraft.world.item.BoneMealItem;
//? } else {
/*import net.minecraft.item.BoneMealItem;*/
//? }

public class PoopItem extends BoneMealItem {
    public PoopItem() {
        super(new Properties().stacksTo(64));
    }
}
package com.rempler.jagahm;

import net.minecraft.item.Item;
//? if >1.21.1 {
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
//? } else {
/*import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;*/
//? }

import java.util.function.Function;

public class ModItems {
    public static final Item POOP = //? if <1.21.2 {
    /* registerItem("poop", new PoopItem()); */
    //? } else {
        registerItem("poop", PoopItem::new, new Item.Settings().maxCount(64));
    //? }

    //? if <1.21.2 {
    /*public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, JAGAHM.id(JAGAHM.MODID, name), item);
    }*/
    //? } else {
    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, JAGAHM.id(JAGAHM.MODID, name));
        return Items.register(itemKey, itemFactory, settings.registryKey(itemKey));
    }
    //? }

    public static void registerModItems() {
        JAGAHM.LOGGER.info("Registering Mod Items for " + JAGAHM.MODID);
    }
}

package com.rempler.jagahm;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item POOP = registerItem("poop", PoopItem::new, new Item.Properties().stacksTo(64));

    public static Item registerItem(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, JAGAHM.id(JAGAHM.MODID, name));
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void registerModItems() {
        JAGAHM.LOGGER.info("Registering Mod Items for " + JAGAHM.MODID);
    }
}

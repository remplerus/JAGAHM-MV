package com.rempler.jagahm;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final Item POOP = //? if <1.21 {
    /* registerItem("poop", new PoopItem()); */
    //? } else {
        registerItem("poop", PoopItem::new, new Item.Settings().maxCount(64));
    //? }
    public static final SoundEvent FART = registerSoundEvent("fart");

    //? if <1.21 {
    /*public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, JAGAHM.id(JAGAHM.MODID, name), item);
    }*/
    //? } else {
    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(JAGAHM.MODID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }
    //? }

    public static void registerModItems() {
        JAGAHM.LOGGER.info("Registering Mod Items for " + JAGAHM.MODID);
    }

    public static SoundEvent registerSoundEvent(String name) {
        Identifier soundID = JAGAHM.id(JAGAHM.MODID, name);
        SoundEvent soundEvent = SoundEvent.of(soundID);
        Registry.register(Registries.SOUND_EVENT, soundID, soundEvent);
        return soundEvent;
    }
}

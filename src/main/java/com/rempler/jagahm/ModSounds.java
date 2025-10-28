package com.rempler.jagahm;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent FART = registerSoundEvent("fart");

    public static void registerModSounds() {
        JAGAHM.LOGGER.info("Registering Mod Sounds for " + JAGAHM.MODID);
    }

    public static SoundEvent registerSoundEvent(String name) {
        Identifier soundID = JAGAHM.id(JAGAHM.MODID, name);
        SoundEvent soundEvent = SoundEvent.of(soundID);
        Registry.register(Registries.SOUND_EVENT, soundID, soundEvent);
        return soundEvent;
    }
}

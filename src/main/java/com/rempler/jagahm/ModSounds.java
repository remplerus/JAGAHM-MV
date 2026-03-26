package com.rempler.jagahm;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent FART = registerSoundEvent("fart");

    public static void registerModSounds() {
        JAGAHM.LOGGER.info("Registering Mod Sounds for " + JAGAHM.MODID);
    }

    public static SoundEvent registerSoundEvent(String name) {
        Identifier soundID = JAGAHM.id(JAGAHM.MODID, name);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundID);
        Registry.register(BuiltInRegistries.SOUND_EVENT, soundID, soundEvent);
        return soundEvent;
    }
}

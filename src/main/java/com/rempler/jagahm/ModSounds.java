package com.rempler.jagahm;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
//? if > 1.16.5 {
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
//? } else {
/*import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;*/
//? }

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JAGAHM.MOD_ID);
    //? if >1.19.2 {
    public static final RegistryObject<SoundEvent> FART = SOUND_EVENTS.register("fart", () -> SoundEvent.createVariableRangeEvent(JAGAHM.rl("fart")));
    //? } else {
    /*public static final RegistryObject<SoundEvent> FART = SOUND_EVENTS.register("fart", () -> new SoundEvent(JAGAHM.rl("fart")));*/
    //? }

    public static void init(IEventBus eventBus) {
        JAGAHM.LOG.info("Registering sounds");
        SOUND_EVENTS.register(eventBus);
    }
}
package com.rempler.jagahm;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, JAGAHM.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> FART = SOUND_EVENTS.register("fart", () -> SoundEvent.createVariableRangeEvent(JAGAHM.rl("fart")));

    public static void init(IEventBus eventBus) {
        JAGAHM.LOG.info("Registering sounds");
        SOUND_EVENTS.register(eventBus);
    }
}

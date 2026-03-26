package com.rempler.jagahm;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class JAGAHMClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModSounds.registerModSounds();
    }

    public static boolean doPoops(Level level, Player player) {
        if (ModConfig.shouldPoopSoundPlay()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART, SoundSource.PLAYERS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}

package com.rempler.jagahm;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class JAGAHMClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModSounds.registerModSounds();
    }

    public static boolean doPoops(World level, PlayerEntity player) {
        if (ModConfig.shouldPoopSoundPlay()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}

package com.rempler.jagahm.compat;

import com.rempler.jagahm.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AgriCraftCompat {
    private AgriCraftCompat(){}
    private static final RandomSource rand = RandomSource.create();

    public static void initAgriCompat(Level level, BlockPos blockPos, Player player) {
        //? if agricraft {
        /*Optional<AgriCrop> optional = AgriApi.getCrop(level, blockPos);
        if (optional.isPresent()) {
            AgriCrop crop = optional.get();
            if (rand.nextDouble() < Config.getGrowSpeed()*Config.getRandomSpeed()) {
                if (!crop.isFertile() && !Config.shouldCheckFertile()) {
                    crop.setGrowthStage(crop.getGrowthStage().getNext(crop, rand));
                } else {
                    crop.applyGrowthTick();
                    JAGAHMNeo.spawnParticles(player, level, blockPos);
                }
            }
        }*/
        //?}
    }
}

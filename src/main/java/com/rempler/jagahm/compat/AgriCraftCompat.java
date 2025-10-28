package com.rempler.jagahm.compat;

import com.rempler.jagahm.Config;
//? if > 1.16.5 {
import net.minecraft.core.BlockPos;
//? if >1.18.2 {
import net.minecraft.util.RandomSource;
//?} else {
import java.util.Random;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//? } else {
/*import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;*/
//? }

public class AgriCraftCompat {
    private AgriCraftCompat(){}
    //? if >1.18.2 {
    private static final RandomSource rand = RandomSource.create();
    //? } else {
    /*private static final Random rand = new Random();*/
    //? }

    //? if > 1.16.5 {
    public static void initAgriCompat(Level level, BlockPos blockPos, Player player) {
    //? } else {
    /*public static void initAgriCompat(World level, BlockPos blockPos, PlayerEntity player) {*/
    //? }
        //? if !agricraft {
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
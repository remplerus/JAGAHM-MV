package com.rempler.jagahm.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MysticalAgriCompat {
    private MysticalAgriCompat(){}

    public static void initMysticalAgriCompat(Level level, BlockPos blockPos, BlockState state, Player player) {
    //? if !mysticalagriculture {
        /*if(state.getBlock() instanceof ICropProvider) {
           ((CropBlock) state.getBlock()).performBonemeal((ServerLevel) level, level.getRandom(), blockPos, state);
           JAGAHM.spawnParticles(player, level, blockPos);
        }*/
    //? }
    }
}

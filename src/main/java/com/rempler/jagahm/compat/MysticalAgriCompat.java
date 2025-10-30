package com.rempler.jagahm.compat;

//? if mysticalagriculture {
    //? if > 1.16.5 {
import com.blakebr0.mysticalagriculture.api.crop.ICropProvider;
    //? } else {
/*import com.blakebr0.mysticalagriculture.api.crop.ICrop;*/
    //? }
//? }

import com.rempler.jagahm.JAGAHM;

//? if > 1.16.5 {
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
//? } else {
/*import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.block.CropsBlock;*/
//? }
public class MysticalAgriCompat {
    private MysticalAgriCompat(){}

    public static void initMysticalAgriCompat(
        //? if > 1.16.5 {
        Level level, BlockPos blockPos, BlockState state, Player player
        //? } else {
        /*World level, BlockPos blockPos, BlockState state, PlayerEntity player*/
        //? }
    ) {
    //? if > 1.16.5 {
        //? if mysticalagriculture {
        if(state.getBlock() instanceof ICropProvider) {
            ((ICropProvider) state.getBlock()).getCrop().getCropBlock().performBonemeal((ServerLevel) level, level.getRandom(), blockPos, state);
            JAGAHM.spawnParticles(player, level, blockPos);
        }
        //? }
    //? } else {
        //? if mysticalagriculture {
        /*if(state.getBlock() instanceof ICrop) {
            ((ICrop) state.getBlock()).getCrop().performBonemeal((ServerWorld) level, level.getRandom(), blockPos, state);
            JAGAHM.spawnParticles(player, level, blockPos);
        }*/
        //? }
    //?}
    }
}
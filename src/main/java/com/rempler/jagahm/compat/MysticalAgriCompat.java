package com.rempler.jagahm.compat;

//? if > 1.16.5 {
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
//? } else {
/*import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;*/
//? }
public class MysticalAgriCompat {
    private MysticalAgriCompat(){}

    //? if > 1.16.5 {
    public static void initMysticalAgriCompat(Level level, BlockPos blockPos, BlockState state, Player player) {
    //? } else {
    /*public static void initMysticalAgriCompat(World level, BlockPos blockPos, BlockState state, PlayerEntity player) {*/
    //? }
        //? if !mysticalagriculture {
        /*if(state.getBlock() instanceof ICropProvider) {
           ((CropBlock) state.getBlock()).performBonemeal((ServerLevel) level, level.getRandom(), blockPos, state);
           JAGAHM.spawnParticles(player, level, blockPos);
        }*/
        //?}
    }
}
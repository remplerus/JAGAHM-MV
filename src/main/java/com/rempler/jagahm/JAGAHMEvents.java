package com.rempler.jagahm;

import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
//? if <1.21 {
/*import net.neoforged.neoforge.event.TickEvent;*/
//? } else {
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//? }

public class JAGAHMEvents {
    //? if <1.21 {
    /*public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Level level = event.player.level();
        Player player = event.player;*/
    //? } else {
    public static void playerTickEvent(PlayerTickEvent event) {
        Level level = event.getEntity().level();
        Player player = event.getEntity();
    //? }
        if (player.isShiftKeyDown() || player.isSprinting()) {
            if (Config.shouldTwerk() && level.getRandom().nextDouble() < Config.getRandomSpeed() && JAGAHM.tickCounter >= Config.getGrowSpeed()) {
                if (level.isClientSide()) {
                    return;
                }
                JAGAHM.tickCounter = 0;
                JAGAHM.applyGrowing(player);
            }
            if (Config.shouldPoopSpawn() && player.isShiftKeyDown()) {
                JAGAHM.fartCounter++;
                if (JAGAHM.fartCounter > 200) {
                    if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                        if (level.isClientSide()) {
                            if (JAGAHM.hasPlayedSound) {
                                return;
                            }
                            if (Config.shouldPoopSoundPlay()) {
                                level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                            }
                            JAGAHM.hasPlayedSound = true;
                        } else if (JAGAHM.hasPlayedSound) {
                            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(JAGAHM.POOP.get())));
                            JAGAHM.fartCounter = 0;
                            JAGAHM.hasPlayedSound = false;
                        }
                    }
                }
            }
            JAGAHM.tickCounter++;
        }
    }

    public static void onBonemealEvent(BonemealEvent event) {
        if (event.getStack().is(JAGAHM.POOP.get())) {
            //? if <1.21 {
            /*if (!(event.getBlock().is(BlockTags.CROPS) || event.getBlock().is(BlockTags.SAPLINGS) || event.getBlock().is(JAGAHM.WHITELIST))) {*/
            //? } else {
            if (!(event.getState().is(BlockTags.CROPS) || event.getState().is(BlockTags.SAPLINGS) || event.getState().is(JAGAHM.WHITELIST))) {
            //? }
                event.setCanceled(true);
            }
        }
    }

    public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = JAGAHM.onRightClick(event.getEntity(), event.getHand(), event.getLevel(), event.getHitVec());

        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}

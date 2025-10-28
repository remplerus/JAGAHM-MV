package com.rempler.jagahm;

//? if >1.16.5 {
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? } else {
/*import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;*/
//? }
import net.minecraft.tags.BlockTags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class JAGAHMEvents {
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        //? if > 1.16.5 {
        Player player = event.player;
        //? } else {
        /*PlayerEntity player = event.player;*/
        //? }
        //? if > 1.19.4 {
        Level level = player.level();
        //? } else if > 1.16.5 {
        /*Level level = player.getLevel();*/
        //? } else {
        /*World level = player.level;*/
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
                                level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART.get(),
                                        //? if > 1.16.5 {
                                        SoundSource.PLAYERS,
                                        //? } else {
                                        /*SoundCategory.PLAYERS,*/
                                        //? }
                                        1.0f, 1.0f);
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
        //? if > 1.16.5 {
        if (event.getStack().is(JAGAHM.POOP.get())) {
        //? } else {
        /*if (event.getStack().sameItem(JAGAHM.POOP.get().getDefaultInstance())) {*/
        //? }
            if (!(event.getBlock().is(BlockTags.CROPS) || event.getBlock().is(BlockTags.SAPLINGS) || event.getBlock().is(JAGAHM.WHITELIST))) {
                event.setCanceled(true);
            }
        }
    }

    public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        //? if >1.18.2 {
        InteractionResult result = JAGAHM.onRightClick(event.getEntity(), event.getHand(), event.getLevel(), event.getHitVec());

        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
        }
        //? } else if >1.16.5{
        /*if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            InteractionResult result = JAGAHM.onRightClick(player, event.getHand(), player.getLevel(), event.getHitVec());

            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
            }
        }*/
        //? } else {
        /*if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ActionResultType result = JAGAHM.onRightClick(player, event.getHand(), player.level, event.getHitVec());

            if (result != ActionResultType.PASS) {
                event.setCancellationResult(result);
            }
        }*/
        //? }
    }
}
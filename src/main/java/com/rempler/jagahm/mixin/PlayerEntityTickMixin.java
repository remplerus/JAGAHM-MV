package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.ModConfig;
import com.rempler.jagahm.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityTickMixin {
    @Inject(method = "playerTick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        onTick();
    }

    public void onTick() {
        @SuppressWarnings("DataFlowIssue") ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        World level = player.getEntityWorld();

        if (player.isSneaking() || player.isSprinting()) {
            if (ModConfig.shouldTwerk() && level.getRandom().nextDouble() < ModConfig.getRandomSpeed() && JAGAHM.tickCounter >= ModConfig.getGrowSpeed()) {
                if (level.isClient()) {
                    return;
                }
                JAGAHM.tickCounter = 0;
                JAGAHM.applyGrowing(player);
            }
            if (ModConfig.shouldPoopSpawn() && player.isSneaking()) {
                JAGAHM.fartCounter++;
                if (JAGAHM.fartCounter > 200) {
                    if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                        if (level.isClient()) {
                            if (JAGAHM.hasPlayedSound) {
                                return;
                            }
                            if (ModConfig.shouldPoopSoundPlay()) {
                                level.playSound(player, player.getX(), player.getY(), player.getZ(), ModItems.FART, SoundCategory.PLAYERS, 1.0f, 1.0f);
                            }
                            JAGAHM.hasPlayedSound = true;
                        } else if (JAGAHM.hasPlayedSound) {
                            level.spawnEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.BONE_MEAL))); //TODO: Check if this works
                            JAGAHM.fartCounter = 0;
                            JAGAHM.hasPlayedSound = false;
                        }
                    }
                }
            }
            JAGAHM.tickCounter++;
        }
    }
}

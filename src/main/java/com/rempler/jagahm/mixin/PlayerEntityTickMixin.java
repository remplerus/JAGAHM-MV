package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.JAGAHMClient;
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
        //? if >1.21.5 && <1.21.9 {
        /*World level = player.getWorld();*/
        //? } else {
        World level = player.getEntityWorld();
        //? }

        if (player.isSneaking() || player.isSprinting()) {
            if (ModConfig.shouldTwerk() && level.getRandom().nextDouble() < ModConfig.getRandomSpeed() && JAGAHM.tickCounter >= ModConfig.getGrowSpeed()) {
                if (level.isClient()) {
                    return;
                }
                JAGAHM.tickCounter = 0;
                JAGAHM.applyGrowing(player);
            }
            if (player.isSneaking()) {
                JAGAHM.doPooping(level, player);
            }
            JAGAHM.tickCounter++;
        }
    }
}

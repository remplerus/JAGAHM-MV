package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.ModConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class PlayerEntityTickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        onTick();
    }

    public void onTick() {
        @SuppressWarnings("DataFlowIssue") ServerPlayer player = (ServerPlayer) (Object) this;
        Level level = player.level();

        if (player.isCrouching() || player.isSprinting()) {
            if (ModConfig.shouldTwerk() && level.getRandom().nextDouble() < ModConfig.getRandomSpeed() && JAGAHM.tickCounter >= ModConfig.getGrowSpeed()) {
                if (level.isClientSide()) {
                    return;
                }
                JAGAHM.tickCounter = 0;
                JAGAHM.applyGrowing(player);
            }
            if (player.isCrouching()) {
                JAGAHM.doPooping(level, player);
            }
            JAGAHM.tickCounter++;
        }
    }
}

package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
    private void onInteractBlockInternal(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult interactionResult = JAGAHM.onRightClick(localPlayer, interactionHand, localPlayer.level(), result);

        if (interactionResult != InteractionResult.PASS) {
            cir.setReturnValue(interactionResult);
            cir.cancel();
        }
    }
}

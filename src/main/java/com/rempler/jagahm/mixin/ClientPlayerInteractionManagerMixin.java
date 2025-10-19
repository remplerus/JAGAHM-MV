package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "interactBlockInternal", at = @At("HEAD"), cancellable = true)
    private void onInteractBlockInternal(ClientPlayerEntity localPlayer, Hand interactionHand, BlockHitResult result, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult interactionResult = JAGAHM.onRightClick(localPlayer, interactionHand, localPlayer.getEntityWorld(), result);

        if (interactionResult != ActionResult.PASS) {
            cir.setReturnValue(interactionResult);
            cir.cancel();
        }
    }
}

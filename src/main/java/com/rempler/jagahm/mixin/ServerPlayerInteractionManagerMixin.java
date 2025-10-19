package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ServerPlayerEntity player, World level, ItemStack stack, Hand hand, BlockHitResult result, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult interactionResult = JAGAHM.onRightClick(player, hand, player.getEntityWorld(), result);

        if (interactionResult != ActionResult.PASS) {
            cir.setReturnValue(interactionResult);
            cir.cancel();
        }
    }
}

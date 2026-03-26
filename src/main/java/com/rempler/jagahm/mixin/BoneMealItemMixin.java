package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void onUseOnFertilizable(ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(pos);
        if (stack.is(ModItems.POOP)) { //TODO: Check if this works
            if (!(blockState.is(BlockTags.CROPS) || blockState.is(BlockTags.SAPLINGS) || blockState.is(JAGAHM.WHITELIST))) {
                cir.setReturnValue(false);
            }
        }
    }
}
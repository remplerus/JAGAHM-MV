package com.rempler.jagahm.mixin;

import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(method = "useOnFertilizable", at = @At("HEAD"), cancellable = true)
    private static void onUseOnFertilizable(ItemStack stack, World level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(pos);
        if (stack.isOf(Items.BONE_MEAL)) { //TODO: Check if this works
            if (!(blockState.isIn(BlockTags.CROPS) || blockState.isIn(BlockTags.SAPLINGS) || blockState.isIn(JAGAHM.WHITELIST))) {
                cir.setReturnValue(false);
            }
        }
    }
}
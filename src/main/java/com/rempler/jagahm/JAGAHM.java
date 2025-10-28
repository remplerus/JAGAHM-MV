package com.rempler.jagahm;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

//? if <1.21.5 {
/*import net.minecraft.server.network.ServerPlayerEntity;*/
//? }

public class JAGAHM implements ModInitializer {
    public static final String VERSION = /*$ mod_version*/ "2.0.0";
    public static final String MINECRAFT = /*$ minecraft*/ "1.21.10";
    public static final String MODID = "jagahm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final TagKey<Block> WHITELIST = TagKey.of(Registries.BLOCK.getKey(), id(MODID, "whitelist"));
    public static final TagKey<Block> BLACKLIST = TagKey.of(Registries.BLOCK.getKey(), id(MODID, "blacklist"));
    public static boolean hasPlayedSound = false;
    public static int tickCounter = 0;
    public static int fartCounter = 0;

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        ModItems.registerModItems();
    }

    /**
     * Adapts to the {@link Identifier} changes introduced in 1.21.
     */
    public static Identifier id(String namespace, String path) {
        //? if <1.21 {
        /*return new Identifier(namespace, path);
        *///?} else
        return Identifier.of(namespace, path);
    }

    public static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.with(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.withAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.with(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    public static void dropStacks(BlockState state, ServerWorld level, BlockPos pos, Entity entity,
                                  ItemStack toolStack) {
        Item replant = new ItemStack(state.getBlock().asItem()).getItem();
        final boolean[] removedReplant = { false };
        Block.getDroppedStacks(state, level, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }
            Block.dropStack(level, pos, stack);
        });
        state.onStacksDropped(level, pos, toolStack, true);
    }

    public static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        //? if <1.21 {
        /*if (block instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        } else if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state);
        } else if (block instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) >= Properties.AGE_3_MAX;
        } else if (block instanceof SugarCaneBlock) {
            return state.get(SugarCaneBlock.AGE) >= Properties.AGE_15_MAX;
        }
        return false;*/
        //?} else {
        return switch (block) {
            case CocoaBlock ignored -> state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMature(state);
            case NetherWartBlock ignored2 -> state.get(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            case SugarCaneBlock ignored3 -> state.get(SugarCaneBlock.AGE) >= Properties.AGE_15_MAX;
            default -> false;
        };
        //? }
    }

    public static ActionResult onRightClick(PlayerEntity player, Hand hand, World level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == Hand.OFF_HAND) {
            return ActionResult.PASS;
        }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getStackInHand(hand);

        if (originalBlock instanceof CropBlock || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (isMature(state)) {
                if (!level.isClient()) {
                    ServerWorld serverLevel = (ServerWorld) level;
                    serverLevel.setBlockState(blockHit.getBlockPos(), getReplantState(state));
                    dropStacks(state, serverLevel, blockHit.getBlockPos(), player, player.getStackInHand(hand));
                } else {
                    player.playSound(originalBlock instanceof NetherWartBlock ? SoundEvents.ITEM_NETHER_WART_PLANT : SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                }

                return ActionResult.SUCCESS;
            }
        }
        else if (originalBlock instanceof SugarCaneBlock) {
            if (blockHit.getSide() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
                return ActionResult.PASS;
            }
            int count = 1;
            BlockPos bottom = blockHit.getBlockPos().down();
            while (level.getBlockState(bottom).isOf(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.down();
            }

            if (count == 1 && !level.getBlockState(blockHit.getBlockPos().up()).isOf(Blocks.SUGAR_CANE)) {
                return ActionResult.PASS;
            }

            if (!level.isClient()) {
                level.breakBlock(bottom.up(2), true);
            } else {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static void spawnParticles(PlayerEntity player, ServerWorld level, BlockPos blockPos) {
        double d0 = level.getRandom().nextDouble();
        for (int a = 0; a < 2; a++) {
            //? if <1.21.3 {
            /*level.spawnParticles((ServerPlayerEntity) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);;*/
            //?} else {
            level.spawnParticles(ParticleTypes.CLOUD, false, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
            //? }
        }
    }

    public static void applyGrowing(PlayerEntity player) {
        //? if >1.21.5 && <1.21.9 {
        /*World level = player.getWorld();*/
        //? } else {
        World level = player.getEntityWorld();
        //? }
        BlockPos pos = player.getBlockPos();

        if (level.isClient()) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) level;

        int a = ModConfig.getGrowRange()/2;
        int b = ModConfig.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (serverWorld.getRandom().nextDouble() < ModConfig.getRandomSpeed()) {
                        BlockState state = serverWorld.getBlockState(blockPos);
                        if (!(state.getBlock() instanceof AirBlock ||
                                state.isIn(BLACKLIST))) {
                                standardGrow(player, serverWorld, blockPos, state);
                        }
                    }
                }
            }
        }
    }

    private static void standardGrow(PlayerEntity player, WorldAccess level, BlockPos blockPos, BlockState state) {
        if (state.contains(CropBlock.AGE)) {
            if (isMature(state)) {
                return;
            }
            BoneMealItem.useOnFertilizable(ModItems.POOP.getDefaultStack(), (World) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof Fertilizable) {
            BoneMealItem.useOnFertilizable(ModItems.POOP.getDefaultStack(), (World) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.contains(SugarCaneBlock.AGE)) {
            for (int j = state.get(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    ((World) level).setBlockState(blockPos.up(), state.with(SugarCaneBlock.AGE, 0));
                    level.setBlockState(blockPos, state.with(SugarCaneBlock.AGE, 0), 4);
                } else {
                    int newAge = j + 5;
                    if (newAge > 15) {
                        newAge = 15;
                    }
                    level.setBlockState(blockPos, state.with(SugarCaneBlock.AGE, newAge), 4);
                    break;
                }
            }
        }
    }

    public static void spawnParticles(PlayerEntity player, WorldAccess level, BlockPos blockPos) {
        if (ModConfig.shouldSpawnParticles()) {
            spawnParticles(player, (ServerWorld) level, blockPos);
        }
    }

    public static void doPooping(World level, PlayerEntity player) {
        if (ModConfig.shouldPoopSpawn()) {
            JAGAHM.fartCounter++;
            if (JAGAHM.fartCounter > 200) {
                if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                    JAGAHM.hasPlayedSound = true;
                }
                if (JAGAHM.hasPlayedSound) {
                    level.spawnEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(ModItems.POOP))); //TODO: Check if this works
                    JAGAHM.fartCounter = 0;
                    JAGAHMClient.doPoops(level, player);
                    JAGAHM.hasPlayedSound = false;
                }
            }
        }
    }
}
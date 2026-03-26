package com.rempler.jagahm;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAGAHM implements ModInitializer {
    public static final String VERSION = /*$ mod_version*/ "2.0.0";
    public static final String MINECRAFT = /*$ minecraft*/ "1.21.10";
    public static final String MODID = "jagahm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final TagKey<Block> WHITELIST = TagKey.create(Registries.BLOCK, id(MODID, "whitelist"));
    public static final TagKey<Block> BLACKLIST = TagKey.create(Registries.BLOCK, id(MODID, "blacklist"));
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
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    public static void dropStacks(BlockState state, ServerLevel level, BlockPos pos, Entity entity,
                                  ItemStack toolStack) {
        Item replant = new ItemStack(state.getBlock().asItem()).getItem();
        final boolean[] removedReplant = { false };
        Block.getDrops(state, level, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }
            Block.popResource(level, pos, stack);
        });
        state.spawnAfterBreak(level, pos, toolStack, true);
    }

    public static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CocoaBlock ignored -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMaxAge(state);
            case NetherWartBlock ignored2 -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            case SugarCaneBlock ignored3 -> state.getValue(SugarCaneBlock.AGE) >= BlockStateProperties.MAX_AGE_15;
            default -> false;
        };
    }

    public static InteractionResult onRightClick(Player player, InteractionHand hand, Level level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);

        if (originalBlock instanceof CropBlock || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (isMature(state)) {
                if (!level.isClientSide()) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    serverLevel.setBlockAndUpdate(blockHit.getBlockPos(), getReplantState(state));
                    dropStacks(state, serverLevel, blockHit.getBlockPos(), player, player.getItemInHand(hand));
                } else {
                    player.playSound(originalBlock instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
                }

                return InteractionResult.SUCCESS;
            }
        }
        else if (originalBlock instanceof SugarCaneBlock) {
            if (blockHit.getDirection() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
                return InteractionResult.PASS;
            }
            int count = 1;
            BlockPos bottom = blockHit.getBlockPos().below();
            while (level.getBlockState(bottom).is(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.below();
            }

            if (count == 1 && !level.getBlockState(blockHit.getBlockPos().above()).is(Blocks.SUGAR_CANE)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                level.destroyBlock(bottom.above(2), true);
            } else {
                player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static void spawnParticles(Player player, ServerLevel level, BlockPos blockPos) {
        double d0 = level.getRandom().nextDouble();
        for (int a = 0; a < 2; a++) {
            level.sendParticles(ParticleTypes.CLOUD, false, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
        }
    }

    public static void applyGrowing(Player player) {
        Level level = player.level();
        BlockPos pos = player.getOnPos();

        if (level.isClientSide()) {
            return;
        }
        ServerLevel serverWorld = (ServerLevel) level;

        int a = ModConfig.getGrowRange()/2;
        int b = ModConfig.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (serverWorld.getRandom().nextDouble() < ModConfig.getRandomSpeed()) {
                        BlockState state = serverWorld.getBlockState(blockPos);
                        if (!(state.getBlock() instanceof AirBlock ||
                                state.is(BLACKLIST))) {
                                standardGrow(player, serverWorld, blockPos, state);
                        }
                    }
                }
            }
        }
    }

    private static void standardGrow(Player player, LevelAccessor level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropBlock.AGE)) {
            if (isMature(state)) {
                return;
            }
            BoneMealItem.growCrop(ModItems.POOP.getDefaultInstance(), (Level) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.growCrop(ModItems.POOP.getDefaultInstance(), (Level) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.hasProperty(SugarCaneBlock.AGE)) {
            for (int j = state.getValue(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    ((Level) level).setBlockAndUpdate(blockPos.above(), state.setValue(SugarCaneBlock.AGE, 0));
                    level.setBlock(blockPos, state.setValue(SugarCaneBlock.AGE, 0), 4);
                } else {
                    int newAge = j + 5;
                    if (newAge > 15) {
                        newAge = 15;
                    }
                    level.setBlock(blockPos, state.setValue(SugarCaneBlock.AGE, newAge), 4);
                    break;
                }
            }
        }
    }

    public static void spawnParticles(Player player, LevelAccessor level, BlockPos blockPos) {
        if (ModConfig.shouldSpawnParticles()) {
            spawnParticles(player, (ServerLevel) level, blockPos);
        }
    }

    public static void doPooping(Level level, Player player) {
        if (ModConfig.shouldPoopSpawn()) {
            JAGAHM.fartCounter++;
            if (JAGAHM.fartCounter > 200) {
                if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                    JAGAHM.hasPlayedSound = true;
                }
                if (JAGAHM.hasPlayedSound) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(ModItems.POOP)));
                    JAGAHM.fartCounter = 0;
                    JAGAHMClient.doPoops(level, player);
                    JAGAHM.hasPlayedSound = false;
                }
            }
        }
    }
}
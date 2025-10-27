package com.rempler.jagahm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? if <1.21.3 {
/*import net.neoforged.fml.ModLoadingContext;
import net.minecraft.server.level.ServerPlayer;*/
//? }

@Mod(JAGAHM.MOD_ID)
public class JAGAHM {
    public static final String MOD_ID = "jagahm";
    public static final String MOD_NAME = "JAGAHM";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final TagKey<Block> WHITELIST = TagKey.create(BuiltInRegistries.BLOCK.key(), rl("whitelist"));
    public static final TagKey<Block> BLACKLIST = TagKey.create(BuiltInRegistries.BLOCK.key(), rl("blacklist"));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
    public static final DeferredHolder<Item, PoopItem> POOP = ITEMS.register("poop", PoopItem::new);
    public static boolean hasPlayedSound = false;
    public static int tickCounter = 0;
    public static int fartCounter = 0;

    public JAGAHM(IEventBus eventBus, ModContainer modContainer) {
        //? if <1.20.6 {
        /*ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");*/
        //? } else {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        //? }
        NeoForge.EVENT_BUS.addListener(JAGAHMEvents::playerTickEvent);
        NeoForge.EVENT_BUS.addListener(JAGAHMEvents::onRightClickBlockEvent);
        NeoForge.EVENT_BUS.addListener(JAGAHMEvents::onBonemealEvent);
        ModSounds.init(eventBus);
        ITEMS.register(eventBus);
    }

    public static ResourceLocation rl(String name) {
        //? if <1.21 {
        /*return new ResourceLocation(MOD_ID, name);*/
        //? } else {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        //? }
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
        //? if <1.21.3 {
        /*Item replant = state.getBlock().getCloneItemStack(level, pos, state).getItem();*/
        //? } else {
        Item replant = state.getBlock().getCloneItemStack(level, pos, state, true, null).getItem();
        //? }
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
        //? if <1.21 {
        /*if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        } else if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        } else if (block instanceof SugarCaneBlock) {
            return state.getValue(SugarCaneBlock.AGE) >= BlockStateProperties.MAX_AGE_15;
        }
        return false;*/
        //? } else {
        return switch (block) {
            case CocoaBlock ignored -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMaxAge(state);
            case NetherWartBlock ignored -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            case SugarCaneBlock ignored -> state.getValue(SugarCaneBlock.AGE) >= BlockStateProperties.MAX_AGE_15;
            default -> false;
        };
        //? }
    }

    public static InteractionResult onRightClick(Player player, InteractionHand hand, Level level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);

        if (originalBlock instanceof CropBlock || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (JAGAHM.isMature(state)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(blockHit.getBlockPos(), JAGAHM.getReplantState(state));
                    JAGAHM.dropStacks(state, (ServerLevel) level, blockHit.getBlockPos(), player, player.getItemInHand(hand));
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

    public static void spawnParticles(Player player, Level level, BlockPos blockPos) {
        double d0 = level.getRandom().nextDouble();
        for (int a = 0; a < 2; a++) {
            //TODO: make custom particle
            //? if <1.21.3 {
            /*((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);*/
            //? } else {
            ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, false, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
            //? }
        }
    }

    public static void applyGrowing(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        if (level.isClientSide()) {
            return;
        }

        int a = Config.getGrowRange()/2;
        int b = Config.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState state = level.getBlockState(blockPos);
                    if (level.getRandom().nextDouble() < Config.getRandomSpeed()) {
                        if (!(state.getBlock() instanceof AirBlock || state.is(JAGAHM.BLACKLIST))) {
                            standardGrow(player, level, blockPos, state);
                        }
                    }
                }
            }
        }
    }

    public static void standardGrow(Player player, LevelAccessor level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropBlock.AGE)) {
            if (JAGAHM.isMature(state)) {
                return;
            }
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.hasProperty(SugarCaneBlock.AGE)) {
            for (int j = state.getValue(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    ((Level) level).setBlockAndUpdate(blockPos.above(), state.setValue(SugarCaneBlock.AGE, 0));
                    //? if <1.20.6 {
                    /*net.neoforged.neoforge.common.CommonHooks.onCropsGrowPost((Level) level, blockPos.above(), state);*/
                    //? } else {
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost((Level) level, blockPos.above(), state);
                    //? }
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
        if (Config.shouldSpawnParticles()) {
            JAGAHM.spawnParticles(player, (Level) level, blockPos);
        }
    }
}
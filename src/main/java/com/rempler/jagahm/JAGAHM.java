package com.rempler.jagahm;

//? if > 1.16.5 {
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.server.level.ServerPlayer;
//? } else {
/*import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;*/
//? }
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rempler.jagahm.compat.AgriCraftCompat;
import com.rempler.jagahm.compat.MysticalAgriCompat;

@SuppressWarnings("removal")
@Mod(JAGAHM.MOD_ID)
public class JAGAHM {
    public static final String MOD_ID = "jagahm";
    public static final String MOD_NAME = "JAGAHM";
    //? if >1.16.5 {
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final TagKey<Block> WHITELIST = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), rl("whitelist"));
    public static final TagKey<Block> BLACKLIST = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), rl("blacklist"));
    //? } else {
    /*public static final Tags.IOptionalNamedTag<Block> WHITELIST = BlockTags.createOptional(rl("whitelist"));
    public static final Tags.IOptionalNamedTag<Block> BLACKLIST = BlockTags.createOptional(rl("blacklist"));
    public static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();*/
    //? }
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<PoopItem> POOP = ITEMS.register("poop", PoopItem::new);
    public static boolean hasPlayedSound = false;
    public static int tickCounter = 0;
    public static int fartCounter = 0;

    //? if >1.18.2 {
    public JAGAHM(FMLJavaModLoadingContext context) {
    //? } else {
    /*public JAGAHM() {
    FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();*/
    //? }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        MinecraftForge.EVENT_BUS.addListener(JAGAHMEvents::playerTickEvent);
        MinecraftForge.EVENT_BUS.addListener(JAGAHMEvents::onRightClickBlockEvent);
        MinecraftForge.EVENT_BUS.addListener(JAGAHMEvents::onBonemealEvent);
        IEventBus eventBus = context.getModEventBus();
        ModSounds.init(eventBus);
        ITEMS.register(eventBus);
    }

    public static ResourceLocation rl(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        //? if >1.16.5 {
        } else if (state.getBlock() instanceof CropBlock) {
            CropBlock cropBlock = (CropBlock) state.getBlock();
        //? } else {
        /*} else if (state.getBlock() instanceof CropsBlock) {
            CropsBlock cropBlock = (CropsBlock) state.getBlock();*/
        //? }
            return cropBlock.getStateForAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    public static void dropStacks(BlockState state, /*? if >1.16.5{*/ServerLevel/*?}else{*//*ServerWorld*//*?}*/ level, BlockPos pos, Entity entity,
                                  ItemStack toolStack) {
        Item replant = state.getBlock().getCloneItemStack(level, pos, state).getItem();
        final boolean[] removedReplant = { false };
        Block.getDrops(state, level, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }
            Block.popResource(level, pos, stack);
        });
        state.spawnAfterBreak(level, pos, toolStack/*? if >1.18.2 {*/, true/*?}*/);
    }

    public static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CocoaBlock) {
            //? if > 1.16.5 {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        } else if (state.getBlock() instanceof CropBlock) {
            CropBlock cropBlock = (CropBlock) state.getBlock();
            return cropBlock.isMaxAge(state);
        } else if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        } else if (block instanceof SugarCaneBlock) {
            return state.getValue(SugarCaneBlock.AGE) >= BlockStateProperties.MAX_AGE_15;
            //? } else {
            /*return state.getValue(CocoaBlock.AGE) >= 2;
        } else if (state.getBlock() instanceof CropsBlock) {
            CropsBlock cropBlock = (CropsBlock) state.getBlock();
            return cropBlock.isMaxAge(state);
        } else if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        } else if (block instanceof SugarCaneBlock) {
            return state.getValue(SugarCaneBlock.AGE) >= 15;*/
            //? }
        }
        return false;
    }

    //? if > 1.16.5 {
    public static InteractionResult onRightClick(Player player, InteractionHand hand, Level level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
    //? } else {
    /*public static ActionResultType onRightClick(PlayerEntity player, Hand hand, World level, BlockRayTraceResult blockHit) {
        if (player.isSpectator() || hand == Hand.OFF_HAND) {
            return ActionResultType.PASS;
        }*/
    //? }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);

        if (originalBlock instanceof /*? if >1.16.5{*/CropBlock/*?}else{*//*CropsBlock*//*?}*/ || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (JAGAHM.isMature(state)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(blockHit.getBlockPos(), JAGAHM.getReplantState(state));
                    JAGAHM.dropStacks(state, (/*? if >1.16.5{*/ServerLevel/*?}else{*//*ServerWorld*//*?}*/) level, blockHit.getBlockPos(), player, player.getItemInHand(hand));
                } else {
                    player.playSound(originalBlock instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
                }
                //? if >1.16.5 {
                return InteractionResult.SUCCESS;
                //? } else {
                /*return ActionResultType.SUCCESS;*/
                //? }
            }
        }
        else if (originalBlock instanceof SugarCaneBlock) {
            if (blockHit.getDirection() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
                //? if >1.16.5 {
                return InteractionResult.PASS;
                //? } else {
                /*return ActionResultType.PASS;*/
                //? }
            }
            int count = 1;
            BlockPos bottom = blockHit.getBlockPos().below();
            while (level.getBlockState(bottom).is(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.below();
            }

            if (count == 1 && !level.getBlockState(blockHit.getBlockPos().above()).is(Blocks.SUGAR_CANE)) {
                //? if >1.16.5 {
                return InteractionResult.PASS;
                //? } else {
                /*return ActionResultType.PASS;*/
                //? }
            }

            if (!level.isClientSide()) {
                level.destroyBlock(bottom.above(2), true);
            } else {
                player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
            }

            //? if >1.16.5 {
            return InteractionResult.SUCCESS;
            //? } else {
            /*return ActionResultType.SUCCESS;*/
            //? }
        }

        //? if >1.16.5 {
        return InteractionResult.PASS;
        //? } else {
        /*return ActionResultType.PASS;*/
        //? }
    }

    //? if >1.16.5 {
    public static void spawnParticles(Player player, Level level, BlockPos blockPos) {
        double d0 = level.getRandom().nextDouble();
        for (int a = 0; a < 2; a++) {
            //TODO: make custom particle
            ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
        }
    }
    //? }

    public static void applyGrowing(/*? if > 1.16.5 {*/Player/*? } else {*//*PlayerEntity*//*? }*/player) {
        /*? if > 1.19.4 {*/Level level = player.level();/*? } else if > 1.16.5{*//*Level level = player.getLevel();*//*? } else {*//*World level = player.level;*///? }
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

    //? if > 1.16.5 {
    public static void standardGrow(Player player, LevelAccessor level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropBlock.AGE)) {
    //? } else {
    /*public static void standardGrow(PlayerEntity player, World level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropsBlock.AGE)) {*/
    //? }
            if (JAGAHM.isMature(state)) {
                return;
            }
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), /*? if >1.16.5 {*/(Level)/*?}*/ level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof /*? if >1.16.5 {*/BonemealableBlock/*?} else {*//*IGrowable*//*?}*/) {
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), /*? if >1.16.5 {*/(Level)/*?}*/ level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.hasProperty(SugarCaneBlock.AGE)) {
            for (int j = state.getValue(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    (/*? if >1.16.5 {*/(Level)/*?}*/ level).setBlockAndUpdate(blockPos.above(), state.setValue(SugarCaneBlock.AGE, 0));
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(/*? if >1.16.5 {*/(Level)/*?}*/ level, blockPos.above(), state);
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
        } else if (ModList.get().isLoaded("agricraft")) {
            AgriCraftCompat.initAgriCompat(/*? if >1.16.5 {*/(Level)/*?}*/ level, blockPos, player);
        } else if (ModList.get().isLoaded("mysticalagriculture")) {
            MysticalAgriCompat.initMysticalAgriCompat(/*? if >1.16.5 {*/(Level)/*?}*/ level, blockPos, state, player);
        }
    }

    //? if > 1.16.5 {
    public static void spawnParticles(Player player, LevelAccessor level, BlockPos blockPos) {
        if (Config.shouldSpawnParticles()) {
            JAGAHM.spawnParticles(player, (Level) level, blockPos);
        }
    }
    //? } else {
    /*public static void spawnParticles(PlayerEntity player, World level, BlockPos blockPos) {
        if (Config.shouldSpawnParticles()) {
            double d0 = level.getRandom().nextDouble();
            for (int a = 0; a < 2; a++) {
                //TODO: make custom particle
                ((ServerWorld) level).sendParticles((ServerPlayerEntity) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                        blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
            }
        }
    }*/
    //? }
}
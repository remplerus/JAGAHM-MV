package com.rempler.jagahm;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.DoubleValue RANDOM_SPEED;
    private static final ForgeConfigSpec.IntValue GROW_SPEED;
    private static final ForgeConfigSpec.IntValue GROW_RANGE;
    private static final ForgeConfigSpec.IntValue GROW_HEIGHT;
    private static final ForgeConfigSpec.BooleanValue MYST_AGRI;
    private static final ForgeConfigSpec.BooleanValue ENABLE_POSTS;
    private static final ForgeConfigSpec.BooleanValue SPAWN_PARTICLES;
    private static final ForgeConfigSpec.BooleanValue AGRICRAFT;
    private static final ForgeConfigSpec.BooleanValue TWERKING;
    private static final ForgeConfigSpec.BooleanValue POOP;
    private static final ForgeConfigSpec.BooleanValue POOP_SOUND;
    private static final ForgeConfigSpec.BooleanValue CHECK_FERTILE;

    public static Double getRandomSpeed() { return RANDOM_SPEED.get(); }
    public static int getGrowSpeed() { return GROW_SPEED.get(); }
    public static int getGrowRange() { return GROW_RANGE.get(); }
    public static int getGrowHeight() { return GROW_HEIGHT.get(); }
    public static boolean activateMystAgri() { return MYST_AGRI.get(); }
    public static boolean activateAgriCraft() { return AGRICRAFT.get(); }
    public static boolean enablePosts() { return ENABLE_POSTS.get(); }
    public static boolean shouldSpawnParticles() { return SPAWN_PARTICLES.get(); }
    public static boolean shouldTwerk() { return TWERKING.get(); }
    public static boolean shouldPoopSpawn() { return POOP.get(); }
    public static boolean shouldPoopSoundPlay() { return POOP_SOUND.get(); }
    public static boolean shouldCheckFertile() { return CHECK_FERTILE.get(); }

    static {
        BUILDER.push("Grow speed");
        RANDOM_SPEED = BUILDER.comment("defines how lucky you are with growing ").defineInRange("random_speed", 0.1, 0, 1);
        GROW_SPEED = BUILDER.comment("defines how fast the random_speed should trigger, executes every here defined ticks").defineInRange("grow_speed", 10, 1, 10000);
        BUILDER.pop();
        BUILDER.push("Grow range");
        GROW_RANGE = BUILDER.defineInRange("grow_range", 4, 1, 128);
        BUILDER.pop();
        BUILDER.push("Grow height");
        GROW_HEIGHT = BUILDER.defineInRange("grow_height", 2, 1, 64);
        BUILDER.pop();
        BUILDER.push("Activate Mystical Agriculture");
        MYST_AGRI = BUILDER.define("activate_myst_agri", true);
        BUILDER.pop();
        BUILDER.push("Activate AgriCraft");
        AGRICRAFT = BUILDER.define("activate_agricraft", true);
        BUILDER.pop();
        BUILDER.push("Enable posts");
        ENABLE_POSTS = BUILDER.define("enable_posts", false);
        BUILDER.pop();
        BUILDER.push("Should spawn particles");
        SPAWN_PARTICLES = BUILDER.define("spawn_particles", true);
        BUILDER.pop();
        BUILDER.push("Should twerking work");
        TWERKING = BUILDER.define("twerking", true);
        BUILDER.pop();
        BUILDER.push("Should poop spawn");
        POOP = BUILDER.define("poop", true);
        BUILDER.pop();
        BUILDER.push("Should poop sound play");
        POOP_SOUND = BUILDER.define("poop_sound", true);
        BUILDER.pop();
        BUILDER.push("Should check for fertile crops (only affects AgriCraft)");
        CHECK_FERTILE = BUILDER.define("check_fertile", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
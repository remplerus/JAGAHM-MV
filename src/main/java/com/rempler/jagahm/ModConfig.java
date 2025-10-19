package com.rempler.jagahm;

public class ModConfig {
    private static final boolean twerkEnabled = true;
    private static final boolean poopSound = true;
    private static final boolean poopSpawn = true;
    private static final boolean spawnParticles = true;
    private static final int growSpeed = 20;
    private static final double randomSpeed = 0.1;
    private static final int growRange = 4;
    private static final int growHeight = 2;
    private static final boolean enablePosts = false;

    public static boolean shouldTwerk() {
        return twerkEnabled;
    }
    public static boolean shouldPoopSpawn() {
        return poopSpawn;
    }
    public static boolean shouldPoopSoundPlay() {
        return poopSound;
    }
    public static int getGrowSpeed() {
        return growSpeed;
    }
    public static double getRandomSpeed() {
        return randomSpeed;
    }

    public static int getGrowRange() {
        return growRange;
    }

    public static int getGrowHeight() {
        return growHeight;
    }

    public static boolean enablePosts() {
        return enablePosts;
    }

    public static boolean shouldSpawnParticles() {
        return spawnParticles;
    }
}

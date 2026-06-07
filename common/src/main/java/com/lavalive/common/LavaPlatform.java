package com.lavalive.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

/**
 * Access class holding the implementation of ILavaPlatform.
 */
public class LavaPlatform {
    private static ILavaPlatform instance;

    public static void init(ILavaPlatform platform) {
        instance = platform;
    }

    public static ResourceLocation loc(String namespace, String path) {
        if (instance == null) {
            throw new IllegalStateException("LavaPlatform has not been initialized!");
        }
        return instance.createResourceLocation(namespace, path);
    }

    public static void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
        if (instance != null) {
            instance.playSound(level, x, y, z, sound, volume, pitch);
        }
    }

    public static void saveConfig(double particleIntensity, double bubbleFrequency, double soundVolumeMultiplier, boolean enableLargeBubbles, boolean performanceMode, double overworldMultiplier, boolean enableEruptions, boolean testMode, boolean particlesDealDamage, double damageCooldown, double magmaFragmentStayTime, boolean enablePopSounds, boolean enableSmallBubblePopSounds, boolean enableRumbleSounds, boolean enablePlayerLavaInteraction, boolean enableRegularBubbles) {
        if (instance != null) {
            instance.saveConfig(particleIntensity, bubbleFrequency, soundVolumeMultiplier, enableLargeBubbles, performanceMode, overworldMultiplier, enableEruptions, testMode, particlesDealDamage, damageCooldown, magmaFragmentStayTime, enablePopSounds, enableSmallBubblePopSounds, enableRumbleSounds, enablePlayerLavaInteraction, enableRegularBubbles);
        }
    }
}

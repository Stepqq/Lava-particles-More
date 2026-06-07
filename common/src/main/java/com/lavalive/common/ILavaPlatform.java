package com.lavalive.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

/**
 * Interface to isolate version-specific Minecraft/Forge APIs.
 */
public interface ILavaPlatform {
    /**
     * Helper to create a ResourceLocation.
     * Minecraft 1.20.1 uses 'new ResourceLocation(namespace, path)'
     * Minecraft 1.21.1 uses 'ResourceLocation.fromNamespaceAndPath(namespace, path)'
     */
    ResourceLocation createResourceLocation(String namespace, String path);

    /**
     * Plays a sound client-side.
     * In both versions this uses level.playLocalSound, but with potential minor signature adjustments.
     */
    void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch);

    /**
     * Saves the configured values back to the platform config.
     */
    void saveConfig(double particleIntensity, double bubbleFrequency, double soundVolumeMultiplier, boolean enableLargeBubbles, boolean performanceMode, double overworldMultiplier, boolean enableEruptions, boolean testMode, boolean particlesDealDamage, double damageCooldown, double magmaFragmentStayTime, boolean enablePopSounds, boolean enableSmallBubblePopSounds, boolean enableRumbleSounds, boolean enablePlayerLavaInteraction, boolean enableRegularBubbles);
}

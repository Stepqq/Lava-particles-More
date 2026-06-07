package com.lavalive.common;

/**
 * Stores active configuration values. Values are updated during mod initialization
 * and reload from the respective platform's configuration files.
 */
public class LavaConfig {
    public static double particleIntensity = 1.0;
    public static double bubbleFrequency = 1.0;
    public static double soundVolumeMultiplier = 1.0;
    public static boolean enableLargeBubbles = true;
    public static boolean performanceMode = false;
    public static double netherMultiplier = 1.5;
    public static double basaltDeltasMultiplier = 2.5;
    public static double overworldMultiplier = 1.0;
    public static boolean enableEruptions = true;
    public static boolean testMode = false;
    public static boolean particlesDealDamage = false;
    public static double damageCooldown = 3.0; // in seconds
    public static double magmaFragmentStayTime = 8.0; // in seconds
    public static boolean enablePopSounds = true;
    public static boolean enableSmallBubblePopSounds = true;
    public static boolean enableRumbleSounds = true;
    public static boolean creativeEruptionPreset = true;
    public static boolean enablePlayerLavaInteraction = true;
    public static boolean enableRegularBubbles = true;
}

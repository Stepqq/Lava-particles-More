package com.lavalive.forge20;

import com.lavalive.common.LavaConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "lavalive", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Forge20Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final ForgeConfigSpec.DoubleValue PARTICLE_INTENSITY;
    public static final ForgeConfigSpec.DoubleValue BUBBLE_FREQUENCY;
    public static final ForgeConfigSpec.DoubleValue SOUND_VOLUME_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LARGE_BUBBLES;
    public static final ForgeConfigSpec.BooleanValue PERFORMANCE_MODE;
    public static final ForgeConfigSpec.DoubleValue NETHER_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue BASALT_DELTAS_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue OVERWORLD_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ERUPTIONS;
    public static final ForgeConfigSpec.BooleanValue TEST_MODE;
    public static final ForgeConfigSpec.BooleanValue PARTICLES_DEAL_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_COOLDOWN;
    public static final ForgeConfigSpec.DoubleValue MAGMA_FRAGMENT_STAY_TIME;
    public static final ForgeConfigSpec.BooleanValue ENABLE_POP_SOUNDS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SMALL_BUBBLE_POP_SOUNDS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RUMBLE_SOUNDS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PLAYER_LAVA_INTERACTION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_REGULAR_BUBBLES;

    static {
        BUILDER.push("General Visuals");
        PARTICLE_INTENSITY = BUILDER.comment("Intensity / spawn amount multiplier for secondary splash particles (0.0 to 20.0).")
                .defineInRange("particleIntensity", 1.0, 0.0, 20.0);
        BUBBLE_FREQUENCY = BUILDER.comment("Bubble rate and check frequency (0.0 to 20.0). Higher values spawn more bubbles.")
                .defineInRange("bubbleFrequency", 1.0, 0.0, 20.0);
        ENABLE_LARGE_BUBBLES = BUILDER.comment("Whether to enable rare large magma bubbles rising and popping.")
                .define("enableLargeBubbles", true);
        ENABLE_ERUPTIONS = BUILDER.comment("Whether to enable violent boiling lava surges (eruptions) inside larger lava lakes.")
                .define("enableEruptions", true);
        PERFORMANCE_MODE = BUILDER.comment("Performance mode. Reduces block sampling frequency to lower CPU overhead.")
                .define("performanceMode", false);
        TEST_MODE = BUILDER.comment("Forces immediate, extremely intensive lava bubbles for visual testing purposes.")
                .define("testMode", false);
        PARTICLES_DEAL_DAMAGE = BUILDER.comment("Causes landing hot magma droplets to deal 0.5 HP fire damage to players nearby.")
                .define("particlesDealDamage", false);
        DAMAGE_COOLDOWN = BUILDER.comment("Cooldown on splash fire damage in seconds (0.0 to 10.0).")
                .defineInRange("damageCooldown", 3.0, 0.0, 10.0);
        MAGMA_FRAGMENT_STAY_TIME = BUILDER.comment("Time landing magma fragments stay on the ground in seconds (0.0 to 30.0).")
                .defineInRange("magmaFragmentStayTime", 8.0, 0.0, 30.0);
        ENABLE_PLAYER_LAVA_INTERACTION = BUILDER.comment("Spawns steam, smoke, and sizzling sounds when players fall into lava.")
                .define("enablePlayerLavaInteraction", true);
        ENABLE_REGULAR_BUBBLES = BUILDER.comment("Whether to enable standard ambient lava bubbles rising and popping.")
                .define("enableRegularBubbles", true);
        BUILDER.pop();

        BUILDER.push("Audio Settings");
        SOUND_VOLUME_MULTIPLIER = BUILDER.comment("Lava bubble sound volume multiplier (0.0 to 20.0).")
                .defineInRange("soundVolumeMultiplier", 1.0, 0.0, 20.0);
        ENABLE_POP_SOUNDS = BUILDER.comment("Whether to enable magma bubble pop sound effects.")
                .define("enablePopSounds", true);
        ENABLE_SMALL_BUBBLE_POP_SOUNDS = BUILDER.comment("Whether to enable quiet popping sounds for small ambient bubbles.")
                .define("enableSmallBubblePopSounds", true);
        ENABLE_RUMBLE_SOUNDS = BUILDER.comment("Whether to enable volcanic/eruption rumbling sound effects.")
                .define("enableRumbleSounds", true);
        BUILDER.pop();

        BUILDER.push("Environmental Multipliers");
        NETHER_MULTIPLIER = BUILDER.comment("Lava bubble rate multiplier inside Nether biomes.")
                .defineInRange("netherMultiplier", 1.5, 0.0, 20.0);
        BASALT_DELTAS_MULTIPLIER = BUILDER.comment("Lava bubble rate multiplier inside Basalt Delta biomes.")
                .defineInRange("basaltDeltasMultiplier", 2.5, 0.0, 20.0);
        OVERWORLD_MULTIPLIER = BUILDER.comment("Lava bubble rate multiplier inside Overworld biomes.")
                .defineInRange("overworldMultiplier", 1.0, 0.0, 20.0);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        update();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        update();
    }

    private static void update() {
        LavaConfig.particleIntensity = PARTICLE_INTENSITY.get();
        LavaConfig.bubbleFrequency = BUBBLE_FREQUENCY.get();
        LavaConfig.soundVolumeMultiplier = SOUND_VOLUME_MULTIPLIER.get();
        LavaConfig.enableLargeBubbles = ENABLE_LARGE_BUBBLES.get();
        LavaConfig.performanceMode = PERFORMANCE_MODE.get();
        LavaConfig.netherMultiplier = NETHER_MULTIPLIER.get();
        LavaConfig.basaltDeltasMultiplier = BASALT_DELTAS_MULTIPLIER.get();
        LavaConfig.overworldMultiplier = OVERWORLD_MULTIPLIER.get();
        LavaConfig.enableEruptions = ENABLE_ERUPTIONS.get();
        LavaConfig.testMode = TEST_MODE.get();
        LavaConfig.particlesDealDamage = PARTICLES_DEAL_DAMAGE.get();
        LavaConfig.damageCooldown = DAMAGE_COOLDOWN.get();
        LavaConfig.magmaFragmentStayTime = MAGMA_FRAGMENT_STAY_TIME.get();
        LavaConfig.enablePopSounds = ENABLE_POP_SOUNDS.get();
        LavaConfig.enableSmallBubblePopSounds = ENABLE_SMALL_BUBBLE_POP_SOUNDS.get();
        LavaConfig.enableRumbleSounds = ENABLE_RUMBLE_SOUNDS.get();
        LavaConfig.enablePlayerLavaInteraction = ENABLE_PLAYER_LAVA_INTERACTION.get();
        LavaConfig.enableRegularBubbles = ENABLE_REGULAR_BUBBLES.get();
    }
}

package com.lavalive.fabric;

import com.lavalive.common.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class LavaLiveFabric implements ModInitializer {
    public static final String MODID = "lavalive";

    public static final SimpleParticleType LAVA_BUBBLE = new SimpleParticleType(false) {};
    public static final SimpleParticleType LAVA_BUBBLE_SMALL = new SimpleParticleType(false) {};
    public static final SimpleParticleType MAGMA_FRAGMENT = new SimpleParticleType(false) {};
    public static final SimpleParticleType EMBER_SPARK = new SimpleParticleType(false) {};
    public static final SimpleParticleType VOLCANIC_ASH = new SimpleParticleType(false) {};
    public static final SimpleParticleType HEAT_SHIMMER = new SimpleParticleType(false) {};

    public static SoundEvent LAVA_BUBBLE_POP_SOUND;
    public static SoundEvent LAVA_RUMBLE_SOUND;

    @Override
    public void onInitialize() {
        ResourceLocation popLoc = new ResourceLocation(MODID, "lava_bubble_pop");
        ResourceLocation rumbleLoc = new ResourceLocation(MODID, "lava_rumble");

        LAVA_BUBBLE_POP_SOUND = SoundEvent.createVariableRangeEvent(popLoc);
        LAVA_RUMBLE_SOUND = SoundEvent.createVariableRangeEvent(rumbleLoc);

        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "lava_bubble"), LAVA_BUBBLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "lava_bubble_small"), LAVA_BUBBLE_SMALL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "magma_fragment"), MAGMA_FRAGMENT);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "ember_spark"), EMBER_SPARK);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "volcanic_ash"), VOLCANIC_ASH);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "heat_shimmer"), HEAT_SHIMMER);

        Registry.register(BuiltInRegistries.SOUND_EVENT, popLoc, LAVA_BUBBLE_POP_SOUND);
        Registry.register(BuiltInRegistries.SOUND_EVENT, rumbleLoc, LAVA_RUMBLE_SOUND);

        LavaPlatform.init(new ILavaPlatform() {
            @Override
            public ResourceLocation createResourceLocation(String namespace, String path) {
                return new ResourceLocation(namespace, path);
            }

            @Override
            public void playSound(Level level, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
                level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, volume, pitch, false);
            }

            @Override
            public void saveConfig(double particleIntensity, double bubbleFrequency, double soundVolumeMultiplier, boolean enableLargeBubbles, boolean performanceMode, double overworldMultiplier, boolean enableEruptions, boolean testMode, boolean particlesDealDamage, double damageCooldown, double magmaFragmentStayTime, boolean enablePopSounds, boolean enableSmallBubblePopSounds, boolean enableRumbleSounds, boolean enablePlayerLavaInteraction, boolean enableRegularBubbles) {
                LavaConfig.particleIntensity = particleIntensity;
                LavaConfig.bubbleFrequency = bubbleFrequency;
                LavaConfig.soundVolumeMultiplier = soundVolumeMultiplier;
                LavaConfig.enableLargeBubbles = enableLargeBubbles;
                LavaConfig.performanceMode = performanceMode;
                LavaConfig.overworldMultiplier = overworldMultiplier;
                LavaConfig.enableEruptions = enableEruptions;
                LavaConfig.testMode = testMode;
                LavaConfig.particlesDealDamage = particlesDealDamage;
                LavaConfig.damageCooldown = damageCooldown;
                LavaConfig.magmaFragmentStayTime = magmaFragmentStayTime;
                LavaConfig.enablePopSounds = enablePopSounds;
                LavaConfig.enableSmallBubblePopSounds = enableSmallBubblePopSounds;
                LavaConfig.enableRumbleSounds = enableRumbleSounds;
                LavaConfig.enablePlayerLavaInteraction = enablePlayerLavaInteraction;
                LavaConfig.enableRegularBubbles = enableRegularBubbles;
            }
        });
    }
}

package com.lavalive.neoforge;

import com.lavalive.common.*;
import com.lavalive.common.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod("lavalive")
public class LavaLiveNeoForge {
    public static final String MODID = "lavalive";

    public static final DeferredRegister<ParticleType<?>> PARTICLES = 
            DeferredRegister.create(net.minecraft.core.registries.Registries.PARTICLE_TYPE, MODID);
    
    public static final DeferredRegister<SoundEvent> SOUNDS = 
            DeferredRegister.create(net.minecraft.core.registries.Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LAVA_BUBBLE = 
            PARTICLES.register("lava_bubble", () -> new SimpleParticleType(false));
            
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LAVA_BUBBLE_SMALL = 
            PARTICLES.register("lava_bubble_small", () -> new SimpleParticleType(false));
            
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAGMA_FRAGMENT = 
            PARTICLES.register("magma_fragment", () -> new SimpleParticleType(false));
            
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMBER_SPARK = 
            PARTICLES.register("ember_spark", () -> new SimpleParticleType(false));
            
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLCANIC_ASH = 
            PARTICLES.register("volcanic_ash", () -> new SimpleParticleType(false));
            
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEAT_SHIMMER = 
            PARTICLES.register("heat_shimmer", () -> new SimpleParticleType(false));

    public static final DeferredHolder<SoundEvent, SoundEvent> LAVA_BUBBLE_POP_SOUND = 
            SOUNDS.register("lava_bubble_pop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "lava_bubble_pop")));

    public static final DeferredHolder<SoundEvent, SoundEvent> LAVA_RUMBLE_SOUND = 
            SOUNDS.register("lava_rumble", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "lava_rumble")));

    public LavaLiveNeoForge(IEventBus modEventBus, net.neoforged.fml.ModContainer modContainer) {
        LavaPlatform.init(new ILavaPlatform() {
            @Override
            public ResourceLocation createResourceLocation(String namespace, String path) {
                return ResourceLocation.fromNamespaceAndPath(namespace, path);
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

        // Register Config Screen Factory on NeoForge 1.21.1 ModContainer
        modContainer.registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
            (mc, parent) -> new com.lavalive.common.LavaConfigScreen(parent)
        );

        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        modEventBus.addListener(this::onRegisterParticleProviders);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LAVA_BUBBLE.get(), LavaBubbleParticle.LargeProvider::new);
        event.registerSpriteSet(LAVA_BUBBLE_SMALL.get(), LavaBubbleParticle.SmallProvider::new);
        event.registerSpriteSet(MAGMA_FRAGMENT.get(), MagmaFragmentParticle.Provider::new);
        event.registerSpriteSet(EMBER_SPARK.get(), EmberSparkParticle.Provider::new);
        event.registerSpriteSet(VOLCANIC_ASH.get(), VolcanicAshParticle.Provider::new);
        event.registerSpriteSet(HEAT_SHIMMER.get(), HeatShimmerParticle.Provider::new);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        LavaClientTickHandler.tick();
    }
}

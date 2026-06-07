package com.lavalive.forge20;

import com.lavalive.common.ILavaPlatform;
import com.lavalive.common.LavaClientTickHandler;
import com.lavalive.common.LavaConfig;
import com.lavalive.common.LavaPlatform;
import com.lavalive.common.particle.EmberSparkParticle;
import com.lavalive.common.particle.HeatShimmerParticle;
import com.lavalive.common.particle.LavaBubbleParticle;
import com.lavalive.common.particle.MagmaFragmentParticle;
import com.lavalive.common.particle.VolcanicAshParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("lavalive")
public class LavaLiveForge20 {
    public static final String MODID = "lavalive";

    public static final DeferredRegister<ParticleType<?>> PARTICLES = 
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    
    public static final DeferredRegister<SoundEvent> SOUNDS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // Register particles
    public static final RegistryObject<SimpleParticleType> LAVA_BUBBLE = 
            PARTICLES.register("lava_bubble", () -> new SimpleParticleType(false));
            
    public static final RegistryObject<SimpleParticleType> LAVA_BUBBLE_SMALL = 
            PARTICLES.register("lava_bubble_small", () -> new SimpleParticleType(false));
            
    public static final RegistryObject<SimpleParticleType> MAGMA_FRAGMENT = 
            PARTICLES.register("magma_fragment", () -> new SimpleParticleType(false));
            
    public static final RegistryObject<SimpleParticleType> EMBER_SPARK = 
            PARTICLES.register("ember_spark", () -> new SimpleParticleType(false));
            
    public static final RegistryObject<SimpleParticleType> VOLCANIC_ASH = 
            PARTICLES.register("volcanic_ash", () -> new SimpleParticleType(false));
            
    public static final RegistryObject<SimpleParticleType> HEAT_SHIMMER = 
            PARTICLES.register("heat_shimmer", () -> new SimpleParticleType(false));

    // Register sounds
    public static final RegistryObject<SoundEvent> LAVA_BUBBLE_POP_SOUND = 
            SOUNDS.register("lava_bubble_pop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "lava_bubble_pop")));

    public static final RegistryObject<SoundEvent> LAVA_RUMBLE_SOUND = 
            SOUNDS.register("lava_rumble", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "lava_rumble")));

    public LavaLiveForge20() {
        // Initialize Platform Bridge
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
                Forge20Config.PARTICLE_INTENSITY.set(particleIntensity);
                Forge20Config.BUBBLE_FREQUENCY.set(bubbleFrequency);
                Forge20Config.SOUND_VOLUME_MULTIPLIER.set(soundVolumeMultiplier);
                Forge20Config.ENABLE_LARGE_BUBBLES.set(enableLargeBubbles);
                Forge20Config.PERFORMANCE_MODE.set(performanceMode);
                Forge20Config.OVERWORLD_MULTIPLIER.set(overworldMultiplier);
                Forge20Config.ENABLE_ERUPTIONS.set(enableEruptions);
                Forge20Config.TEST_MODE.set(testMode);
                Forge20Config.PARTICLES_DEAL_DAMAGE.set(particlesDealDamage);
                Forge20Config.DAMAGE_COOLDOWN.set(damageCooldown);
                Forge20Config.MAGMA_FRAGMENT_STAY_TIME.set(magmaFragmentStayTime);
                Forge20Config.ENABLE_POP_SOUNDS.set(enablePopSounds);
                Forge20Config.ENABLE_SMALL_BUBBLE_POP_SOUNDS.set(enableSmallBubblePopSounds);
                Forge20Config.ENABLE_RUMBLE_SOUNDS.set(enableRumbleSounds);
                Forge20Config.ENABLE_PLAYER_LAVA_INTERACTION.set(enablePlayerLavaInteraction);
                Forge20Config.ENABLE_REGULAR_BUBBLES.set(enableRegularBubbles);
                Forge20Config.SPEC.save();
                
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

        // Initialize Mod Configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Forge20Config.SPEC);

        // Register Config Screen Factory
        ModLoadingContext.get().registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(
                (mc, parent) -> new com.lavalive.common.LavaConfigScreen(parent)
            )
        );

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(Forge20Config.class);
        PARTICLES.register(modEventBus);
        SOUNDS.register(modEventBus);

        // Register particle providers event
        modEventBus.register(this);

        // Register client tick handler to Forge event bus as a non-static instance
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LAVA_BUBBLE.get(), LavaBubbleParticle.LargeProvider::new);
        event.registerSpriteSet(LAVA_BUBBLE_SMALL.get(), LavaBubbleParticle.SmallProvider::new);
        event.registerSpriteSet(MAGMA_FRAGMENT.get(), MagmaFragmentParticle.Provider::new);
        event.registerSpriteSet(EMBER_SPARK.get(), EmberSparkParticle.Provider::new);
        event.registerSpriteSet(VOLCANIC_ASH.get(), VolcanicAshParticle.Provider::new);
        event.registerSpriteSet(HEAT_SHIMMER.get(), HeatShimmerParticle.Provider::new);
    }

    public static class ForgeEvents {
        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                LavaClientTickHandler.tick();
            }
        }
    }
}

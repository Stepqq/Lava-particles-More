package com.lavalive.fabric;

import com.lavalive.common.LavaClientTickHandler;
import com.lavalive.common.particle.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class LavaLiveFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register particle providers
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.LAVA_BUBBLE, LavaBubbleParticle.LargeProvider::new);
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.LAVA_BUBBLE_SMALL, LavaBubbleParticle.SmallProvider::new);
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.MAGMA_FRAGMENT, MagmaFragmentParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.EMBER_SPARK, EmberSparkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.VOLCANIC_ASH, VolcanicAshParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(LavaLiveFabric.HEAT_SHIMMER, HeatShimmerParticle.Provider::new);

        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LavaClientTickHandler.tick();
        });
    }
}

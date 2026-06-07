package com.lavalive.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;

public class HeatShimmerParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float originalScale;

    protected HeatShimmerParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        
        // Shimmer rises slowly
        this.xd = xSpeed;
        this.yd = ySpeed != 0 ? ySpeed : 0.01 + this.random.nextFloat() * 0.01;
        this.zd = zSpeed;

        this.lifetime = 20 + this.random.nextInt(15);
        this.originalScale = 0.5F + this.random.nextFloat() * 0.5F;
        this.quadSize = this.originalScale;
        
        this.gravity = -0.02F; // Rises slowly
        this.alpha = 0.15F + this.random.nextFloat() * 0.1F; // Very translucent distortion effect
        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
        this.oRoll = this.roll;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Expand as the hot air dissipates/spreads out
        this.quadSize = this.originalScale * (1.0F + ((float) this.age / this.lifetime) * 1.2F);

        // Jitter horizontally to create a wavy shimmering motion
        this.xd += (this.random.nextFloat() - 0.5F) * 0.008F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.008F;
        this.roll += 0.04F * (this.random.nextBoolean() ? 1.0F : -1.0F);

        this.move(this.xd, this.yd, this.zd);

        // Slow down drift
        this.xd *= 0.9D;
        this.yd *= 0.95D;
        this.zd *= 0.9D;

        // Fade out to zero
        this.alpha = 0.25F * (1.0F - ((float) this.age / this.lifetime));

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HeatShimmerParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

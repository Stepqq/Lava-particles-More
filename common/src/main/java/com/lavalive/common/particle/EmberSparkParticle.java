package com.lavalive.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;

public class EmberSparkParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected EmberSparkParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        // Ember velocity: goes up and spreads slightly
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.lifetime = 10 + this.random.nextInt(15);
        this.quadSize = 0.08F + this.random.nextFloat() * 0.08F;
        
        // Negative gravity so they float upwards
        this.gravity = -0.15F;
        this.alpha = 1.0F;
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

        // Float upwards and drift horizontally with slight noise (ember turbulence)
        this.yd -= 0.04D * this.gravity;
        this.xd += (this.random.nextFloat() - 0.5F) * 0.015F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.015F;
        this.roll += 0.06F * (this.random.nextBoolean() ? 1.0F : -1.0F);

        this.move(this.xd, this.yd, this.zd);

        // Air drag
        this.xd *= 0.95D;
        this.yd *= 0.95D;
        this.zd *= 0.95D;

        // Fade out
        this.alpha = 1.0F - ((float) this.age / this.lifetime);

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880; // Glowing spark
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
            return new EmberSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

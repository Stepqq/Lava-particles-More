package com.lavalive.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;

public class VolcanicAshParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected VolcanicAshParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xd = xSpeed;
        this.yd = ySpeed != 0 ? ySpeed : -0.01 - this.random.nextFloat() * 0.01;
        this.zd = zSpeed;

        this.lifetime = 40 + this.random.nextInt(40);
        this.quadSize = 0.06F + this.random.nextFloat() * 0.08F;
        
        // Very low gravity so it drifts slowly downwards
        this.gravity = 0.08F;
        this.alpha = 0.8F;
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

        // Apply movement and weak gravity
        this.yd -= 0.04D * this.gravity;
        // Drift in wind (slight horizontal changes)
        this.xd += (this.random.nextFloat() - 0.5F) * 0.003F;
        this.zd += (this.random.nextFloat() - 0.5F) * 0.003F;
        this.roll += 0.03F * (this.random.nextBoolean() ? 1.0F : -1.0F);

        this.move(this.xd, this.yd, this.zd);

        // Heavy drag representing atmospheric suspension
        this.xd *= 0.96D;
        this.yd *= 0.96D;
        this.zd *= 0.96D;

        // Fade out
        this.alpha = 0.8F * (1.0F - ((float) this.age / this.lifetime));

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
            return new VolcanicAshParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

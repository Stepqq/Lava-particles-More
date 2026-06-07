package com.lavalive.common.particle;

import com.lavalive.common.LavaConfig;
import com.lavalive.common.LavaPlatform;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class LavaBubbleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float originalScale;
    private final boolean isLarge;

    protected LavaBubbleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, boolean isLarge) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.isLarge = isLarge;
        
        this.xd = xSpeed;
        this.zd = zSpeed;

        if (isLarge) {
            this.yd = ySpeed != 0 ? ySpeed : 0.015 + this.random.nextFloat() * 0.015;
            this.lifetime = 120 + this.random.nextInt(60);
            this.originalScale = 0.35F + this.random.nextFloat() * 0.3F; // Smaller large bubbles
        } else {
            // Small bubbles spawn at surface, rise extremely slowly/jitter and pop quickly
            this.yd = ySpeed != 0 ? ySpeed : 0.005 + this.random.nextFloat() * 0.008;
            this.lifetime = 8 + this.random.nextInt(8);
            this.originalScale = 0.12F + this.random.nextFloat() * 0.12F; // Smaller small bubbles
        }
        
        this.quadSize = this.originalScale;
        this.gravity = 0.0F; // Controlled buoyancy
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            BlockPos checkPos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
            net.minecraft.world.level.block.state.BlockState checkState = this.level.getBlockState(checkPos);
            if (checkState.is(net.minecraft.world.level.block.Blocks.LAVA)) {
                float fluidHeight = checkState.getFluidState().getHeight(this.level, checkPos);
                if (this.y < checkPos.getY() + fluidHeight - 0.15) {
                    this.remove();
                    return;
                }
            }
            this.pop();
            this.remove();
            return;
        }

        float progress = (float) this.age / this.lifetime;
        if (isLarge) {
            // Organic wobbling breathing animation
            float wobble = Mth.sin(progress * (float) Math.PI * 3.5F) * 0.06F;
            this.quadSize = this.originalScale * (1.0F + progress * 0.45F + wobble);
            
            this.xd += (this.random.nextFloat() - 0.5F) * 0.004F;
            this.zd += (this.random.nextFloat() - 0.5F) * 0.004F;
        } else {
            // Small bubble wobbles slightly and jitters
            float wobble = Mth.sin(progress * (float) Math.PI * 2.0F) * 0.04F;
            this.quadSize = this.originalScale * (1.0F + wobble);
            
            this.xd += (this.random.nextFloat() - 0.5F) * 0.008F;
            this.zd += (this.random.nextFloat() - 0.5F) * 0.008F;
        }
        
        this.xd = Mth.clamp(this.xd, -0.03, 0.03);
        this.zd = Mth.clamp(this.zd, -0.03, 0.03);

        this.move(this.xd, this.yd, this.zd);

        // Pop if it exits the lava fluid boundary
        BlockPos pos = new BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
        net.minecraft.world.level.block.state.BlockState state = this.level.getBlockState(pos);
        if (!state.is(net.minecraft.world.level.block.Blocks.LAVA)) {
            this.pop();
            this.remove();
        } else {
            float fluidHeight = state.getFluidState().getHeight(this.level, pos);
            // Allow the bubble to emerge slightly above the surface (e.g. 0.08 blocks) for a better visual pop
            if (this.y >= pos.getY() + fluidHeight + 0.08) {
                this.pop();
                this.remove();
            } else {
                this.setSpriteFromAge(this.sprites);
            }
        }
    }

    private void pop() {
        if (!isLarge) {
            // Play popping sound for small bubbles (quieter and higher pitched)
            if (LavaConfig.enableSmallBubblePopSounds && LavaConfig.enablePopSounds) {
                SoundEvent popSound = BuiltInRegistries.SOUND_EVENT.get(LavaPlatform.loc("lavalive", "lava_bubble_pop"));
                if (popSound != null) {
                    LavaPlatform.playSound(this.level, this.x, this.y, this.z, popSound, (float) (LavaConfig.soundVolumeMultiplier * 0.15F), 1.35F + this.random.nextFloat() * 0.35F);
                }
            }

            // Small bubbles pop quietly with only 1 optional spark
            double intensity = LavaConfig.particleIntensity;
            if (intensity > 0) {
                if (this.random.nextFloat() < 0.3F * intensity) {
                    ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                    if (sparkType instanceof SimpleParticleType) {
                        this.level.addParticle((SimpleParticleType) sparkType, this.x, this.y + 0.02, this.z, 
                            (this.random.nextDouble() - 0.5) * 0.08, 0.06 + this.random.nextDouble() * 0.08, (this.random.nextDouble() - 0.5) * 0.08);
                    }
                }
                // Small chance to eject a single magma fragment that lands nearby
                if (this.random.nextFloat() < 0.15F * intensity) {
                    ParticleType<?> fragType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "magma_fragment"));
                    if (fragType instanceof SimpleParticleType) {
                        this.level.addParticle((SimpleParticleType) fragType, this.x, this.y + 0.02, this.z, 
                            (this.random.nextDouble() - 0.5) * 0.06, 0.06 + this.random.nextDouble() * 0.06, (this.random.nextDouble() - 0.5) * 0.06);
                    }
                }
            }
            return;
        }

        double intensity = LavaConfig.particleIntensity;
        if (intensity <= 0) return;

        // Play popping sound
        if (LavaConfig.enablePopSounds) {
            SoundEvent popSound = BuiltInRegistries.SOUND_EVENT.get(LavaPlatform.loc("lavalive", "lava_bubble_pop"));
            if (popSound != null) {
                LavaPlatform.playSound(this.level, this.x, this.y, this.z, popSound, (float) (LavaConfig.soundVolumeMultiplier * 0.65F), 0.8F + this.random.nextFloat() * 0.4F);
            }
        }

        // Spawn sparks
        int sparks = (int) ((8 + this.random.nextInt(6)) * intensity);
        ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
        if (sparkType instanceof SimpleParticleType) {
            SimpleParticleType simpleType = (SimpleParticleType) sparkType;
            for (int i = 0; i < sparks; i++) {
                double rx = this.x + (this.random.nextDouble() - 0.5) * 0.25;
                double ry = this.y + 0.02;
                double rz = this.z + (this.random.nextDouble() - 0.5) * 0.25;
                double vx = (this.random.nextDouble() - 0.5) * 0.16;
                double vy = 0.12 + this.random.nextDouble() * 0.15;
                double vz = (this.random.nextDouble() - 0.5) * 0.16;
                this.level.addParticle(simpleType, rx, ry, rz, vx, vy, vz);
            }
        }

        // Spawn fragments (Increased count and throwing distance)
        int fragments = (int) ((4 + this.random.nextInt(4)) * intensity);
        ParticleType<?> fragType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "magma_fragment"));
        if (fragType instanceof SimpleParticleType) {
            SimpleParticleType simpleType = (SimpleParticleType) fragType;
            for (int i = 0; i < fragments; i++) {
                double rx = this.x + (this.random.nextDouble() - 0.5) * 0.15;
                double ry = this.y + 0.02;
                double rz = this.z + (this.random.nextDouble() - 0.5) * 0.15;
                double vx = (this.random.nextDouble() - 0.5) * 0.18;
                double vy = 0.12 + this.random.nextDouble() * 0.15;
                double vz = (this.random.nextDouble() - 0.5) * 0.18;
                this.level.addParticle(simpleType, rx, ry, rz, vx, vy, vz);
            }
        }

        // Spawn volcanic ash
        int ash = (int) ((2 + this.random.nextInt(3)) * intensity);
        ParticleType<?> ashType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "volcanic_ash"));
        if (ashType instanceof SimpleParticleType) {
            SimpleParticleType simpleType = (SimpleParticleType) ashType;
            for (int i = 0; i < ash; i++) {
                double rx = this.x + (this.random.nextDouble() - 0.5) * 0.35;
                double ry = this.y + 0.02;
                double rz = this.z + (this.random.nextDouble() - 0.5) * 0.35;
                double vx = (this.random.nextDouble() - 0.5) * 0.03;
                double vy = 0.03 + this.random.nextDouble() * 0.04;
                double vz = (this.random.nextDouble() - 0.5) * 0.03;
                this.level.addParticle(simpleType, rx, ry, rz, vx, vy, vz);
            }
        }

        // Spawn smoke
        for (int i = 0; i < 2; i++) {
            this.level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, 
                this.x + (this.random.nextDouble() - 0.5) * 0.15, 
                this.y + 0.02, 
                this.z + (this.random.nextDouble() - 0.5) * 0.15, 
                0.0, 0.01 + this.random.nextDouble() * 0.015, 0.0);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class LargeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public LargeProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LavaBubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, true);
        }
    }

    public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SmallProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LavaBubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, false);
        }
    }
}

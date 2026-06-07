package com.lavalive.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.core.registries.BuiltInRegistries;
import com.lavalive.common.LavaPlatform;
import com.lavalive.common.LavaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class MagmaFragmentParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int dryTicks = 0;
    private boolean dried = false;
    private static long lastDamageTime = 0;

    protected MagmaFragmentParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Dynamic stay time based on configuration (20 ticks per second)
        int stayTicks = (int) (LavaConfig.magmaFragmentStayTime * 20.0);
        this.lifetime = Math.max(20, stayTicks + this.random.nextInt(30));
        this.quadSize = 0.12F + this.random.nextFloat() * 0.12F;
        
        this.gravity = 0.8F;
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

        // Fluid check: extinguish when hitting water
        net.minecraft.core.BlockPos bp = new net.minecraft.core.BlockPos(
            net.minecraft.util.Mth.floor(this.x),
            net.minecraft.util.Mth.floor(this.y),
            net.minecraft.util.Mth.floor(this.z)
        );
        if (this.level.getFluidState(bp).is(net.minecraft.tags.FluidTags.WATER)) {
            // Spawn steam smoke particles
            for (int k = 0; k < 3; k++) {
                this.level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, 
                    this.x + (this.random.nextDouble() - 0.5) * 0.15, 
                    this.y + 0.1, 
                    this.z + (this.random.nextDouble() - 0.5) * 0.15, 
                    0.0, 0.02, 0.0);
            }
            // Spawn water bubble particles rising upwards
            for (int k = 0; k < 5; k++) {
                this.level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE,
                    this.x + (this.random.nextDouble() - 0.5) * 0.2,
                    this.y - 0.1,
                    this.z + (this.random.nextDouble() - 0.5) * 0.2,
                    (this.random.nextDouble() - 0.5) * 0.02,
                    0.1 + this.random.nextDouble() * 0.1,
                    (this.random.nextDouble() - 0.5) * 0.02
                );
            }
            // Play sizzle sound
            this.level.playLocalSound(this.x, this.y, this.z, 
                net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH, 
                net.minecraft.sounds.SoundSource.BLOCKS, 
                0.2F, 1.2F + this.random.nextFloat() * 0.3F, false);
            this.remove();
            return;
        }

        // Check player collision for fire damage when droplet is hot
        if (LavaConfig.particlesDealDamage && !this.dried && this.age % 4 == 0) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isCreative() && !player.isSpectator()) {
                double dx = this.x - player.getX();
                double dy = this.y - player.getY();
                double dz = this.z - player.getZ();
                double distSq = dx * dx + dy * dy + dz * dz;
                if (distSq < 1.0 * 1.0) { // Droplet hit radius
                    long now = System.currentTimeMillis();
                    long cdMs = (long) (LavaConfig.damageCooldown * 1000.0);
                    if (now - lastDamageTime > cdMs) {
                        lastDamageTime = now;
                        player.animateHurt(0.0F); // Red tint visual flash
                        player.playSound(net.minecraft.sounds.SoundEvents.PLAYER_HURT_ON_FIRE, 0.45F, 1.0F);
                        
                        // Execute damage on the server thread in singleplayer/integrated server
                        net.minecraft.client.server.IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
                        if (server != null) {
                            server.execute(() -> {
                                net.minecraft.server.level.ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
                                if (serverPlayer != null) {
                                    serverPlayer.hurt(serverPlayer.damageSources().onFire(), 1.0F); // 1.0F = half a heart (0.5 heart damage)
                                }
                            });
                        }
                    }
                    this.remove();
                    return;
                }
            }
        }

        if (this.onGround) {
            this.xd = 0.0D;
            this.yd = 0.0D;
            this.zd = 0.0D;
            
            this.dryTicks++;
            float dryLimit = Math.max(10.0F, (float) (LavaConfig.magmaFragmentStayTime * 20.0 * 0.3));
            if (this.dryTicks > dryLimit) {
                this.dried = true;
            }

            // Emit smoke and tiny sparks while hot on the floor
            if (!this.dried && this.random.nextFloat() < 0.08F) {
                if (this.random.nextFloat() < 0.6F) {
                    this.level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, 
                        this.x, this.y + 0.03, this.z, 0, 0.004, 0);
                } else {
                    ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                    if (sparkType instanceof SimpleParticleType) {
                        this.level.addParticle((SimpleParticleType) sparkType, this.x, this.y + 0.03, this.z, 
                            (this.random.nextDouble() - 0.5) * 0.02, 0.03 + this.random.nextDouble() * 0.02, (this.random.nextDouble() - 0.5) * 0.02);
                    }
                }
            }

            // Slow cooling down color interpolation
            float coolingDuration = Math.max(10.0F, (float) (LavaConfig.magmaFragmentStayTime * 20.0 * 0.45));
            float t = Mth.clamp((float) this.dryTicks / coolingDuration, 0.0F, 1.0F);
            this.rCol = Mth.lerp(t, 1.0F, 0.25F);
            this.gCol = Mth.lerp(t, 0.5F, 0.25F);
            this.bCol = Mth.lerp(t, 0.0F, 0.25F);
        } else {
            this.yd -= 0.04D * this.gravity;
            this.roll += 0.05F * (this.random.nextBoolean() ? 1.0F : -1.0F);
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.98D;
            this.yd *= 0.98D;
            this.zd *= 0.98D;
        }

        this.alpha = 1.0F - ((float) this.age / this.lifetime);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public int getLightColor(float partialTick) {
        if (this.dried) {
            return super.getLightColor(partialTick);
        }
        return 15728880;
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
            return new MagmaFragmentParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

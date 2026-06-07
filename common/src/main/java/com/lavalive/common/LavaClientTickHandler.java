package com.lavalive.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class LavaClientTickHandler {
    private static final List<LavaSurge> activeSurges = new ArrayList<>();
    private static final List<LavaFlare> activeFlares = new ArrayList<>();
    private static int tickCounter = 0;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused() || mc.player == null) {
            return;
        }

        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        // Tick active surges and flares every tick for smooth particle physics/spawning
        activeSurges.removeIf(surge -> surge.tick(level));
        activeFlares.removeIf(flare -> flare.tick(level));

        // Player Lava Interaction Check (Sizzling, bubbles, and steam when submerged in lava)
        if (LavaConfig.enablePlayerLavaInteraction && player.isInLava()) {
            if (level.random.nextFloat() < 0.12F) {
                level.playLocalSound(
                    player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.LAVA_EXTINGUISH,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.25F, 0.85F + level.random.nextFloat() * 0.3F, false
                );
            }
            
            double px = player.getX();
            double py = player.getY() + 0.3;
            double pz = player.getZ();
            
            for (int s = 0; s < 2; s++) {
                double rx = px + (level.random.nextDouble() - 0.5) * player.getBbWidth() * 1.2;
                double rz = pz + (level.random.nextDouble() - 0.5) * player.getBbWidth() * 1.2;
                double ry = py + level.random.nextDouble() * player.getBbHeight() * 0.6;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, 
                    rx, ry, rz, 
                    (level.random.nextDouble() - 0.5) * 0.02, 
                    0.05 + level.random.nextDouble() * 0.05, 
                    (level.random.nextDouble() - 0.5) * 0.02
                );
            }

            if (level.random.nextFloat() < 0.3F) {
                ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                if (sparkType instanceof SimpleParticleType) {
                    double rx = px + (level.random.nextDouble() - 0.5) * player.getBbWidth();
                    double rz = pz + (level.random.nextDouble() - 0.5) * player.getBbWidth();
                    level.addParticle((SimpleParticleType) sparkType, rx, py + 0.5, rz, 
                        (level.random.nextDouble() - 0.5) * 0.15, 
                        0.18 + level.random.nextDouble() * 0.15, 
                        (level.random.nextDouble() - 0.5) * 0.15
                    );
                }
            }

            if (LavaConfig.enableRegularBubbles && level.random.nextFloat() < 0.5F) {
                ParticleType<?> smallBubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble_small"));
                if (smallBubbleType instanceof SimpleParticleType) {
                    double rx = px + (level.random.nextDouble() - 0.5) * player.getBbWidth() * 1.5;
                    double rz = pz + (level.random.nextDouble() - 0.5) * player.getBbWidth() * 1.5;
                    level.addParticle((SimpleParticleType) smallBubbleType, rx, py, rz, 0.0, 0.01, 0.0);
                }
            }
        }

        // Optimization: Skip block sampling ticks to reduce getBlockState CPU overhead
        tickCounter++;
        int interval = LavaConfig.performanceMode ? 4 : 2;
        if (tickCounter % interval != 0) {
            return;
        }

        // Tune random check count based on settings
        int baseChecks = 320;
        if (LavaConfig.performanceMode) {
            baseChecks = 100;
        }
        if (LavaConfig.testMode) {
            baseChecks = 1200; // Massively check surroundings for immediate visual response
        }

        int checks = (int) (baseChecks * (LavaConfig.testMode ? 2.5 : LavaConfig.bubbleFrequency)) * interval;
        if (checks <= 0) {
            return;
        }

        // Sampling radius around the player (denser volume)
        int horizontalRadius = LavaConfig.testMode ? 10 : 16; // Narrow radius for test mode concentration
        int verticalRadius = LavaConfig.testMode ? 6 : 8;

        for (int i = 0; i < checks; i++) {
            int rx = player.getBlockX() + level.random.nextInt(horizontalRadius * 2) - horizontalRadius;
            int ry = player.getBlockY() + level.random.nextInt(verticalRadius * 2) - verticalRadius;
            int rz = player.getBlockZ() + level.random.nextInt(horizontalRadius * 2) - horizontalRadius;

            BlockPos pos = new BlockPos(rx, ry, rz);
            
            // Check if chunk is loaded to prevent checking outside of world boundaries
            if (level.hasChunkAt(pos)) {
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.LAVA)) {
                    processLavaBlock(level, pos);
                }
            }
        }
    }

    private static void processLavaBlock(ClientLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState aboveState = level.getBlockState(pos.above());
        
        // The block above must not be lava or water (bubble surface check)
        if (!aboveState.isAir() && (aboveState.is(Blocks.LAVA) || aboveState.is(Blocks.WATER))) {
            return;
        }

        float fluidHeight = state.getFluidState().getHeight(level, pos);

        // Neighbor check to verify pool context
        int lavaNeighbors = 0;
        if (level.getBlockState(pos.north()).is(Blocks.LAVA)) lavaNeighbors++;
        if (level.getBlockState(pos.south()).is(Blocks.LAVA)) lavaNeighbors++;
        if (level.getBlockState(pos.east()).is(Blocks.LAVA)) lavaNeighbors++;
        if (level.getBlockState(pos.west()).is(Blocks.LAVA)) lavaNeighbors++;

        // Check if lava is shallow (only 1 block deep)
        boolean isShallow = !level.getBlockState(pos.below()).is(Blocks.LAVA);

        // Calculate activity factor based on Biome
        double biomeMultiplier = LavaConfig.testMode ? 10.0 : LavaConfig.overworldMultiplier;
        String biomeId = level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("");
        
        if (!LavaConfig.testMode) {
            if (biomeId.contains("delta") || biomeId.contains("basalt_deltas")) {
                biomeMultiplier = LavaConfig.basaltDeltasMultiplier;
            } else if (biomeId.contains("nether") || biomeId.contains("crimson") || biomeId.contains("warped") || biomeId.contains("soul") || biomeId.contains("wastes")) {
                biomeMultiplier = LavaConfig.netherMultiplier;
            }
        }

        double chance = level.random.nextDouble();
        double spawnChanceFactor = biomeMultiplier;
        if (!LavaConfig.testMode) {
            if (isShallow) {
                spawnChanceFactor *= 0.3; // Reduce bubble rate on shallow lava
            }
            if (lavaNeighbors == 0) {
                spawnChanceFactor *= 0.15; // Single blocks bubble occasionally
            } else if (lavaNeighbors < 3) {
                spawnChanceFactor *= 0.6; // Smaller streams bubble slightly less
            }
        }

        // 1. Heat Shimmer particle (common atmospheric visual)
        if (chance < 0.08 * spawnChanceFactor) {
            ParticleType<?> shimmerType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "heat_shimmer"));
            if (shimmerType instanceof SimpleParticleType) {
                double sx = pos.getX() + level.random.nextDouble();
                double sy = pos.getY() + fluidHeight + 0.02;
                double sz = pos.getZ() + level.random.nextDouble();
                level.addParticle((SimpleParticleType) shimmerType, sx, sy, sz, 0, 0.005 + level.random.nextDouble() * 0.008, 0);
            }
        }

        // 2. Small Magma Bubbles (subtle, quick surface pops)
        if (LavaConfig.enableRegularBubbles && chance < 0.038 * spawnChanceFactor) {
            ParticleType<?> smallBubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble_small"));
            if (smallBubbleType instanceof SimpleParticleType) {
                double bx = pos.getX() + level.random.nextDouble();
                double by = pos.getY() + fluidHeight - 0.05;
                double bz = pos.getZ() + level.random.nextDouble();
                level.addParticle((SimpleParticleType) smallBubbleType, bx, by, bz, 0, 0.005, 0);
            }
        }

        // 3. Large Magma Bubbles (slow rise, dramatic pops, only in deeper pools)
        if (!isShallow && LavaConfig.enableLargeBubbles) {
            if (chance < 0.0075 * spawnChanceFactor) {
                ParticleType<?> largeBubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble"));
                if (largeBubbleType instanceof SimpleParticleType) {
                    double bx = pos.getX() + 0.25 + level.random.nextDouble() * 0.5;
                    double depth = 0.5 + level.random.nextDouble() * 0.8;
                    double by = pos.getY() + fluidHeight - depth;
                    double bz = pos.getZ() + 0.25 + level.random.nextDouble() * 0.5;
                    
                    level.addParticle((SimpleParticleType) largeBubbleType, bx, by, bz, 0, 0.01 + level.random.nextDouble() * 0.008, 0);
                }
            }
        }

        // 4. Mini-Eruptions (Violent boiling surges in large lava lakes)
        int maxSurges = LavaConfig.testMode ? 15 : 3;
        if (LavaConfig.enableEruptions && (LavaConfig.testMode || (!isShallow && lavaNeighbors >= 3)) && activeSurges.size() < maxSurges) {
            // Approx 0.04% chance per pool check
            if (chance < 0.0004 * spawnChanceFactor) {
                boolean tooClose = false;
                for (LavaSurge surge : activeSurges) {
                    if (surge.pos.closerThan(pos, LavaConfig.testMode ? 4 : 12)) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose) {
                    activeSurges.add(new LavaSurge(pos, 80 + level.random.nextInt(60)));
                }
            }
        }

        // 5. Rare Lava Flares (Mini eruptive geysers shooting particles high)
        int maxFlares = LavaConfig.testMode ? 10 : 2;
        if (LavaConfig.enableEruptions && (LavaConfig.testMode || (!isShallow && lavaNeighbors >= 3)) && activeFlares.size() < maxFlares) {
            // Approx 0.02% chance per check
            if (chance < 0.0002 * spawnChanceFactor) {
                boolean tooClose = false;
                for (LavaFlare flare : activeFlares) {
                    if (flare.pos.closerThan(pos, LavaConfig.testMode ? 4 : 10)) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose) {
                    activeFlares.add(new LavaFlare(pos, 25 + level.random.nextInt(15), level.random));
                }
            }
        }
    }

    public static class LavaSurge {
        public final BlockPos pos;
        public final int maxAge;
        public int age = 0;

        public LavaSurge(BlockPos pos, int maxAge) {
            this.pos = pos;
            this.maxAge = maxAge;
        }

        public boolean tick(ClientLevel level) {
            this.age++;
            if (this.age >= this.maxAge) {
                return true; // remove
            }

            // High density boiling activity!
            if (level.random.nextFloat() < 0.85F) {
                double sx = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 1.6;
                double sz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 1.6;
                BlockPos pPos = new BlockPos(net.minecraft.util.Mth.floor(sx), pos.getY(), net.minecraft.util.Mth.floor(sz));
                BlockState pState = level.getBlockState(pPos);
                if (pState.is(Blocks.LAVA)) {
                    float pFluidHeight = pState.getFluidState().getHeight(level, pPos);
                    double sy = pPos.getY() + pFluidHeight;

                    // Spawn dense heat shimmers
                    ParticleType<?> shimmerType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "heat_shimmer"));
                    if (shimmerType instanceof SimpleParticleType) {
                        level.addParticle((SimpleParticleType) shimmerType, sx, sy + 0.02, sz, 0, 0.01 + level.random.nextDouble() * 0.015, 0);
                    }

                    // Spawn small boiling bubbles
                    if (LavaConfig.enableRegularBubbles) {
                        ParticleType<?> smallBubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble_small"));
                        if (smallBubbleType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) smallBubbleType, sx, sy - 0.05, sz, 0, 0.005, 0);
                        }
                    }

                    // Spawn aggressive flying sparks!
                    if (level.random.nextFloat() < 0.3F) {
                        ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                        if (sparkType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) sparkType, sx, sy + 0.05, sz, 
                                (level.random.nextDouble() - 0.5) * 0.2, 0.15 + level.random.nextDouble() * 0.2, (level.random.nextDouble() - 0.5) * 0.2);
                        }
                    }
                }
            }

            // Volcanic rumblings
            if (LavaConfig.enableRumbleSounds && this.age % 30 == 1) {
                net.minecraft.sounds.SoundEvent rumbleSound = BuiltInRegistries.SOUND_EVENT.get(LavaPlatform.loc("lavalive", "lava_rumble"));
                if (rumbleSound != null) {
                    LavaPlatform.playSound(level, pos.getX(), pos.getY(), pos.getZ(), rumbleSound, (float) (LavaConfig.soundVolumeMultiplier * 0.45F), 0.85F + level.random.nextFloat() * 0.2F);
                }
            }

            return false;
        }
    }

    public static class LavaFlare {
        public final BlockPos pos;
        public final int maxAge;
        public int age = 0;
        public final double angleOffset;
        public final double tiltIntensity;
        public final double sizeScale;
        public final double shapeVariance;

        public LavaFlare(BlockPos pos, int maxAge, net.minecraft.util.RandomSource random) {
            this.pos = pos;
            this.maxAge = maxAge;
            this.angleOffset = random.nextDouble() * Math.PI * 2.0;
            this.tiltIntensity = random.nextDouble() * 0.08; // not too much tilt
            this.sizeScale = 0.90 + random.nextDouble() * 0.20; // 0.90 to 1.10 size (minimal difference)
            this.shapeVariance = 0.90 + random.nextDouble() * 0.20; // 0.90 to 1.10 shape/sway variation
        }

        public boolean tick(ClientLevel level) {
            this.age++;
            if (this.age >= this.maxAge) {
                return true; // remove
            }

            // 1. Vortex depression: Spawn particles moving inwards to simulate fluid getting sucked in
            if (LavaConfig.enableRegularBubbles && this.age < 15) {
                int inflowCount = 2 + level.random.nextInt(3);
                for (int i = 0; i < inflowCount; i++) {
                    double angle = level.random.nextDouble() * Math.PI * 2.0;
                    double radius = (1.0 + level.random.nextDouble() * 0.8) * this.sizeScale;
                    double sx = pos.getX() + 0.5 + Math.cos(angle) * radius;
                    double sz = pos.getZ() + 0.5 + Math.sin(angle) * radius;
                    
                    BlockPos pPos = new BlockPos(net.minecraft.util.Mth.floor(sx), pos.getY(), net.minecraft.util.Mth.floor(sz));
                    BlockState pState = level.getBlockState(pPos);
                    if (pState.is(Blocks.LAVA)) {
                        float pFluidHeight = pState.getFluidState().getHeight(level, pPos);
                        double sy = pPos.getY() + pFluidHeight + 0.02;

                        // Point velocity vector directly towards the center
                        double vx = (pos.getX() + 0.5 - sx) * 0.12;
                        double vz = (pos.getZ() + 0.5 - sz) * 0.12;

                        // Spawn small bubbles moving inwards and slightly downwards (depression)
                        ParticleType<?> bubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble_small"));
                        if (bubbleType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) bubbleType, sx, sy, sz, vx, -0.01, vz);
                        }
                    }
                }
            }

            // 2. High velocity eruptive column! (Denser as the vortex closes in)
            if (this.age > 4) {
                // Apply a slight direction bias (not too much tilt)
                double tiltX = Math.cos(this.angleOffset) * this.tiltIntensity * (this.age / (double)this.maxAge);
                double tiltZ = Math.sin(this.angleOffset) * this.tiltIntensity * (this.age / (double)this.maxAge);

                if (LavaConfig.creativeEruptionPreset) {
                    // Creative Preset: Solid, twisting heat & fluid geyser plume
                    // Organic sway: column center moves slightly in a slow wavy pattern
                    double swayX = Math.sin(this.age * 0.15 * this.shapeVariance) * 0.12;
                    double swayZ = Math.cos(this.age * 0.15 * this.shapeVariance) * 0.12;
                    double coreX = pos.getX() + 0.5 + swayX + tiltX * 2.0;
                    double coreZ = pos.getZ() + 0.5 + swayZ + tiltZ * 2.0;
                    
                    BlockPos corePos = new BlockPos(net.minecraft.util.Mth.floor(coreX), pos.getY(), net.minecraft.util.Mth.floor(coreZ));
                    BlockState coreState = level.getBlockState(corePos);
                    float coreFluidHeight = coreState.is(Blocks.LAVA) ? coreState.getFluidState().getHeight(level, corePos) : 1.0f;

                    // Spawn dense twisting heat shimmers to form the solid column body
                    ParticleType<?> shimmerType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "heat_shimmer"));
                    if (shimmerType instanceof SimpleParticleType) {
                        for (int i = 0; i < 3; i++) {
                            double twist = this.age * 0.15 * this.shapeVariance + i * 2.0;
                            double ox = Math.cos(twist) * 0.12 * this.sizeScale;
                            double oz = Math.sin(twist) * 0.12 * this.sizeScale;
                            // Slightly vary shimmer sizes (quadSize) and heights
                            level.addParticle((SimpleParticleType) shimmerType, 
                                coreX + ox, 
                                pos.getY() + coreFluidHeight - 0.15 + level.random.nextDouble() * 0.4 * this.sizeScale, 
                                coreZ + oz, 
                                tiltX, (0.20 + level.random.nextDouble() * 0.08) * this.sizeScale, tiltZ);
                        }
                    }

                    // Spawn large magma bubble particles rising up the column core
                    if (LavaConfig.enableLargeBubbles && level.random.nextFloat() < 0.4F) {
                        ParticleType<?> largeBubbleType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "lava_bubble"));
                        if (largeBubbleType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) largeBubbleType, 
                                coreX + (level.random.nextDouble() - 0.5) * 0.15 * this.sizeScale, 
                                pos.getY() + coreFluidHeight - 0.15, 
                                coreZ + (level.random.nextDouble() - 0.5) * 0.15 * this.sizeScale, 
                                tiltX, (0.16 + level.random.nextDouble() * 0.06) * this.sizeScale, tiltZ);
                        }
                    }

                    // Spawn a churning splash base at the lava surface
                    if (level.random.nextFloat() < 0.6F) {
                        ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                        if (sparkType instanceof SimpleParticleType) {
                            double rx = coreX + (level.random.nextDouble() - 0.5) * 0.4 * this.sizeScale;
                            double rz = coreZ + (level.random.nextDouble() - 0.5) * 0.4 * this.sizeScale;
                            level.addParticle((SimpleParticleType) sparkType, 
                                rx, pos.getY() + coreFluidHeight - 0.05, rz, 
                                (level.random.nextDouble() - 0.5) * 0.2 + tiltX, 
                                (0.05 + level.random.nextDouble() * 0.1) * this.sizeScale, 
                                (level.random.nextDouble() - 0.5) * 0.2 + tiltZ);
                        }
                    }

                    // The spray: Magma fragments fan out from the top of the geyser plume (height sways slowly)
                    if (level.random.nextFloat() < 0.7F) {
                        double heightSway = Math.sin(this.age * 0.2 * this.shapeVariance) * 0.4;
                        double sprayHeight = pos.getY() + coreFluidHeight + (3.0 + heightSway) * this.sizeScale + level.random.nextDouble() * 0.8;
                        double angle = level.random.nextDouble() * Math.PI * 2.0;
                        double speed = (0.04 + level.random.nextDouble() * 0.1) * this.sizeScale;
                        double vx = Math.cos(angle) * speed + tiltX * 0.5;
                        double vz = Math.sin(angle) * speed + tiltZ * 0.5;
                        
                        ParticleType<?> magmaType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "magma_fragment"));
                        if (magmaType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) magmaType, 
                                coreX + Math.cos(angle) * 0.15 * this.sizeScale, 
                                sprayHeight, 
                                coreZ + Math.sin(angle) * 0.15 * this.sizeScale, 
                                vx, (-0.04 - level.random.nextDouble() * 0.04) * this.sizeScale, vz);
                        }
                    }
                } else {
                    // Standard Preset: Original explosive particle fountain
                    int particlesCount = 3 + level.random.nextInt(3);
                    if (LavaConfig.testMode) {
                        particlesCount += 2;
                    }
                    
                    BlockState state = level.getBlockState(pos);
                    float fluidHeight = state.is(Blocks.LAVA) ? state.getFluidState().getHeight(level, pos) : 1.0f;

                    for (int p = 0; p < particlesCount; p++) {
                        // Spawn close to the core center
                        double sx = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.25 * this.sizeScale;
                        double sz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.25 * this.sizeScale;
                        double sy = pos.getY() + fluidHeight + 0.02;

                        // Embers shooting high with high vertical speed
                        ParticleType<?> sparkType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "ember_spark"));
                        if (sparkType instanceof SimpleParticleType) {
                            level.addParticle((SimpleParticleType) sparkType, sx, sy, sz, 
                                (level.random.nextDouble() - 0.5) * 0.08 + tiltX, 
                                (0.28 + level.random.nextDouble() * 0.32) * this.sizeScale, 
                                (level.random.nextDouble() - 0.5) * 0.08 + tiltZ);
                        }

                        // Magma fragments thrown high
                        if (level.random.nextFloat() < 0.65F) {
                            ParticleType<?> magmaType = BuiltInRegistries.PARTICLE_TYPE.get(LavaPlatform.loc("lavalive", "magma_fragment"));
                            if (magmaType instanceof SimpleParticleType) {
                                level.addParticle((SimpleParticleType) magmaType, sx, sy, sz, 
                                    (level.random.nextDouble() - 0.5) * 0.18 + tiltX, 
                                    (0.22 + level.random.nextDouble() * 0.28) * this.sizeScale, 
                                    (level.random.nextDouble() - 0.5) * 0.18 + tiltZ);
                            }
                        }
                    }
                }
            }

            // Sound at start and intermittently
            if (LavaConfig.enableRumbleSounds && this.age == 1) {
                net.minecraft.sounds.SoundEvent rumbleSound = BuiltInRegistries.SOUND_EVENT.get(LavaPlatform.loc("lavalive", "lava_rumble"));
                if (rumbleSound != null) {
                    LavaPlatform.playSound(level, pos.getX(), pos.getY(), pos.getZ(), rumbleSound, (float) (LavaConfig.soundVolumeMultiplier * 0.9F * this.sizeScale), 1.25F + level.random.nextFloat() * 0.15F);
                }
            }

            return false;
        }
    }
}

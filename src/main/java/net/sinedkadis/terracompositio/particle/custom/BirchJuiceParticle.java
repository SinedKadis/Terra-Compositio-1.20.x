package net.sinedkadis.terracompositio.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sinedkadis.terracompositio.fluid.ModFluids;
import net.sinedkadis.terracompositio.particle.ModParticles;
import org.jetbrains.annotations.NotNull;

public class BirchJuiceParticle extends TextureSheetParticle {
    private final Fluid type = ModFluids.BIRCH_JUICE_FLUID.source.get();
    protected boolean isGlowing;
    BirchJuiceParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ);
        //this.setSize(0.1F, 0.1F);
        this.quadSize *= 0.25F;
        this.gravity = 0.06F;
        this.setSpriteFromAge(spriteSet);
    }

    protected Fluid getType() {
        return this.type;
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float pPartialTick) {
        return this.isGlowing ? 240 : super.getLightColor(pPartialTick);
    }
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (!this.removed) {
            this.yd -= (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.postMoveUpdate();
            if (!this.removed) {
                this.xd *= 0.9800000190734863;
                this.yd *= 0.9800000190734863;
                this.zd *= 0.9800000190734863;
                if (this.type != Fluids.EMPTY) {
                    BlockPos $$0 = BlockPos.containing(this.x, this.y, this.z);
                    FluidState $$1 = this.level.getFluidState($$0);
                    if ($$1.getType() == this.type && this.y < (double)((float)$$0.getY() + $$1.getHeight(this.level, $$0))) {
                        this.remove();
                    }

                }
            }
        }
    }
    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }

    }

    protected void postMoveUpdate() {
    }
    public static TextureSheetParticle createBirchJuiceFallParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
        BirchJuiceParticle $$8 = new BirchJuiceFallAndLandParticle(pLevel, pX, pY, pZ, ModParticles.BIRCH_JUICE_SPLASH_PARTICLE.get(),spriteSet);
        //$$8.setColor(0.2F, 0.3F, 1.0F);
        return $$8;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

    public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return createBirchJuiceFallParticle(level,x,y,z,sprites);
        }
    }
    @OnlyIn(Dist.CLIENT)
    static class BirchJuiceFallAndLandParticle extends FallAndLandParticle {
        BirchJuiceFallAndLandParticle(ClientLevel level, double x, double y, double z, ParticleOptions particleOptions, SpriteSet spriteSet) {
            super(level, x, y, z, particleOptions,spriteSet);
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                SoundEvent $$0 = SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
                float $$1 = Mth.randomBetween(this.random, 0.3F, 1.0F);
                this.level.playLocalSound(this.x, this.y, this.z, $$0, SoundSource.BLOCKS, $$1, 1.0F, false);
            }

        }
    }
    @OnlyIn(Dist.CLIENT)
    private static class FallAndLandParticle extends FallingParticle {
        protected final ParticleOptions landParticle;

        FallAndLandParticle(ClientLevel pLevel, double pX, double pY, double pZ, ParticleOptions pLandParticle,SpriteSet spriteSet) {
            super(pLevel, pX, pY, pZ,spriteSet);
            this.landParticle = pLandParticle;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }

        }
    }
    @OnlyIn(Dist.CLIENT)
    private static class FallingParticle extends BirchJuiceParticle {
        FallingParticle(ClientLevel pLevel, double pX, double pY, double pZ,SpriteSet spriteSet) {
            this(pLevel, pX, pY, pZ, (int)(64.0 / (Math.random() * 0.8 + 0.2)),spriteSet);
        }

        FallingParticle(ClientLevel pLevel, double pX, double pY, double pZ, int pLifetime,SpriteSet spriteSet) {
            super(pLevel, pX, pY, pZ,spriteSet);
            this.lifetime = pLifetime;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }

        }
    }
}

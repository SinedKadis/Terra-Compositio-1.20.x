package net.sinedkadis.terracompositio.mixin;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeLivingEntity;

import net.sinedkadis.terracompositio.effect.ModEffects;
import net.sinedkadis.terracompositio.fluid.ModFluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IForgeLivingEntity {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Shadow
    protected boolean isAffectedByFluids() {
        return true;
    }
    @Shadow
    public boolean canStandOnFluid(FluidState pFluidState) {
        return false;
    }

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Attribute pAttribute);

    @Shadow protected abstract float getWaterSlowDown();

    @Shadow public abstract float getSpeed();

    @Shadow public abstract boolean hasEffect(MobEffect pEffect);

    @Shadow public abstract boolean onClimbable();

    @Shadow public abstract Vec3 getFluidFallingAdjustedMovement(double pGravity, boolean pIsFalling, Vec3 pDeltaMovement);

    @Inject(method = "travel",at = @At("RETURN"))
    protected void onTravelInFlow(Vec3 pTravelVector,CallbackInfo ci) {
        if (this.isControlledByLocalInstance()) {
            //Vec3 horizontalVec = new Vec3(pTravelVector.x,0,pTravelVector.z);
            Vec3 deltaBoosted = new Vec3(this.getDeltaMovement().x,
                    this.getDeltaMovement().y > 0 ? this.getDeltaMovement().y*3:this.getDeltaMovement().y/2,
                    this.getDeltaMovement().z);
            double d0 = 0.08D;
            boolean flag = deltaBoosted.y <= 0.0D;
            FluidState fluidstate = this.level().getFluidState(this.blockPosition());
            if (((fluidstate.getFluidType() == ModFluids.FLOW_FLUID.type.get()) && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate))
                    || this.hasEffect(ModEffects.FLOW_SATURATION.get())) {

                if (((fluidstate.getFluidType() == ModFluids.FLOW_FLUID.type.get()) && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate))
                        && this.hasEffect(ModEffects.FLOW_SATURATION.get())) {
                    //horizontalVec = new Vec3(pTravelVector.x*2,pTravelVector.y*2,pTravelVector.z*2);
                    deltaBoosted = new Vec3(this.getDeltaMovement().x*2,
                                this.getDeltaMovement().y > 0 ? this.getDeltaMovement().y*6:this.getDeltaMovement().y,
                            this.getDeltaMovement().z*2);
                    //TODO achievement
                }
                double d9 = this.getY();
                float f4 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float f5 = 0.02F;
                float f6 = (float) 1;


                if (!this.onGround()) {
                    f6 *= 0.5F;
                }

                f4 += (0.54600006F - f4) * f6 / 3.0F;
                f5 += (this.getSpeed() - f5) * f6 / 3.0F;

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f4 = 0.96F;
                }

                f5 *= (float) this.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
                this.moveRelative(f5, pTravelVector);
                this.move(MoverType.SELF, deltaBoosted);
                Vec3 vec36 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    vec36 = new Vec3(vec36.x, 0.2D, vec36.z);
                }

                this.setDeltaMovement(vec36.multiply(f4, 0.8F, f4));
                Vec3 vec32 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                this.setDeltaMovement(vec32);
                if (this.horizontalCollision && this.isFree(vec32.x, vec32.y + (double) 0.6F - this.getY() + d9, vec32.z)) {
                    this.setDeltaMovement(vec32.x, 0.3F, vec32.z);
                }
            }

        }
    }
}
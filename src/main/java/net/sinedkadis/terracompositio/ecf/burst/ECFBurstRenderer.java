package net.sinedkadis.terracompositio.ecf.burst;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ECFBurstRenderer extends EntityRenderer<ECFBurstProjectileEntity> {
    private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;

    public ECFBurstRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public void render(ECFBurstProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        int tickCount = pEntity.tickCount;
        if (tickCount >= 1 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(pEntity) < MIN_CAMERA_DISTANCE_SQUARED)) {
            int cfe = pEntity.getECF();
            int count = TCInnerConfig.RENDER_COUNT_FUNCTION.applyAsInt(cfe);
            if (count > 100)
                count = 100;
            if (count < 1) count = 1;
            Vector3f[] offsets1 = getOffsets(pEntity);
            if (offsets1 == null || offsets1.length < count) {
                try {
                    generateOffsets(pEntity, count);
                } catch (RuntimeException e) {
                    return;
                }
            }
            offsets1 = getOffsets(pEntity);
            assert offsets1 != null;
            var renderType = RenderType.entityTranslucentEmissive(getTextureLocation(pEntity));
            var buffer = pBuffer.getBuffer(renderType);

            boolean isEnd = tickCount >= pEntity.getTimeToLive();
            float pDelta = (float) (tickCount - pEntity.getTimeToLive()) / 40f;

            for (int i = 0; i < count; i++) {

                pPoseStack.pushPose();

                var offset = offsets1[i];
                float oX = offset.x();
                float oY = offset.y() + (pEntity.getBbHeight() / 2);
                float oZ = offset.z();

                if (isEnd) {
                    pPoseStack.translate(
                            Mth.lerp(pDelta, oX, -oX * 0.1f),
                            Mth.lerp(pDelta, oY, -oY * 0.1f),
                            Mth.lerp(pDelta, oZ, -oZ * 0.1f)
                    );
                } else {
                    pPoseStack.translate(oX, oY, oZ);
                }
                pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                ParticleHelperInternal.drawEcfParticle(pPoseStack, pPackedLight, buffer);

                pPoseStack.popPose();
            }
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }
    }

    private void generateOffsets(ECFBurstProjectileEntity entity, int count) {
        if (count > 100000) throw new RuntimeException("Particles amount is suspicious large: " + count);
        Vector3f[] offsets1 = getOffsets(entity);
        if (offsets1 == null || offsets1.length < count) {
            offsets1 = new Vector3f[(int) (double) count];
            for (int i = 0; i < count; i++) {
                offsets1[i] = ParticleHelperInternal.getSpreadParticleOffset(entity.level().random, count).toVector3f();
            }
            setOffsets(entity,offsets1);
        }
    }


    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(ECFBurstProjectileEntity pEntity) {
        return TerraCompositio.modLoc("textures/particle/ecf_particle.png");
    }

    @Nullable
    public Vector3f[] getOffsets(ECFBurstProjectileEntity pEntity) {
        return pEntity.getOffsets();
    }

    public void setOffsets(ECFBurstProjectileEntity pEntity, Vector3f[] offsets) {
        pEntity.setOffsets(offsets);
    }
}
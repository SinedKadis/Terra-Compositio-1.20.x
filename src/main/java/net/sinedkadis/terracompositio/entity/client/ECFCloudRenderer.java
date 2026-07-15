package net.sinedkadis.terracompositio.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.entity.custom.ECFCloudEntity;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ECFCloudRenderer extends EntityRenderer<ECFCloudEntity> {
    private Vector3f[] offsets;


    public ECFCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ECFCloudEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        var renderType = RenderType.entityTranslucentEmissive(getTextureLocation(pEntity));
        var buffer = pBuffer.getBuffer(renderType);
        int cfe = pEntity.getSyncedECF();
        float k = (float) Math.log10(cfe);
        int count = (int) (cfe * 0.1f);
        if (count < 0) return;
        if (offsets == null || offsets.length < count) genOffsets(pEntity);
        pPoseStack.pushPose();
        int radius = pEntity.getRange(true);
        pPoseStack.translate(0, radius, 0);
        var frustum = Minecraft.getInstance().levelRenderer.getFrustum();
        float size = 0.1f;
        for (int i = 0; i < count; i++) {
            var offset = offsets[i];
            float oX = offset.x();
            float oY = offset.y();
            float oZ = offset.z();

            double x = pEntity.getX();
            double y = pEntity.getY() + radius;
            double z = pEntity.getZ();
            AABB aabb = new AABB(
                    x + oX * k - size,
                    y + oY * k - size,
                    z + oZ * k - size,
                    x + oX * k + size,
                    y + oY * k + size,
                    z + oZ * k + size
            );

            if (!frustum.isVisible(aabb)) continue;


            pPoseStack.pushPose();
            pPoseStack.translate(oX*k, oY*k, oZ*k);
            pPoseStack.mulPose(entityRenderDispatcher.camera.rotation());
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            ParticleHelperInternal.drawEcfParticle(pPoseStack, pPackedLight, buffer);
            pPoseStack.popPose();
        }
        pPoseStack.popPose();

    }




    private void genOffsets(ECFCloudEntity entity) {
        int cfe = entity.getSyncedECF();
        float count = cfe * 0.1f;
        if (offsets == null || offsets.length < count) {
            offsets = new Vector3f[(int) Math.ceil(count)];
            for (int i = 0; i < count; i++) {
                offsets[i] = ParticleHelperInternal.getSpreadParticleOffset(entity.level().random, (int) (count)).toVector3f();
            }
        }
    }


    /**
     * Returns the location of an entity's texture.
     */
    @Override
    public ResourceLocation getTextureLocation(ECFCloudEntity pEntity) {
        return TerraCompositio.modLoc("textures/particle/ecf_particle.png");
    }
}
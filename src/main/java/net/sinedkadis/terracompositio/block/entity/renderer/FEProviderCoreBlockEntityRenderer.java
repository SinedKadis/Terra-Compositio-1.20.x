package net.sinedkadis.terracompositio.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.entity.FEProviderCoreBlockEntity;
import net.sinedkadis.terracompositio.entity.client.ECFCubeModel;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.registries.TCModelLayers;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sinedkadis.terracompositio.entity.client.FlowCedarEntRenderer.CUBE_TEXTURE;

@ParametersAreNonnullByDefault
public class FEProviderCoreBlockEntityRenderer implements BlockEntityRenderer<FEProviderCoreBlockEntity> {
    public final ECFCubeModel<FlowCedarEntEntity> ecfCubeModel;

    public FEProviderCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.ecfCubeModel = new ECFCubeModel<>(context.bakeLayer(TCModelLayers.ECF_CUBE_LAYER));
    }


    @Override
    public void render(FEProviderCoreBlockEntity pBlockEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        if (level == null) return;
        BlockPos origin = pBlockEntity.getBlockPos();
        IECFHandler capability = pBlockEntity.getECFCapability(null);
        IEnergyStorage energyCapability = pBlockEntity.getEnergyCapability(null);

        int ecf = capability.getECF();
        if (ecf > 0) {
            renderCube(partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, level);
            if (!pBlockEntity.exception.hasExceptions()) {
                if (level.getGameTime() % 5 == 0 && ((energyCapability.getMaxEnergyStored() - energyCapability.getEnergyStored()) > 0)) {
                    for (BlockPos pylonPos : pBlockEntity.getPylonPoses()) {
                        ParticleHelperInternal.spawnSpiralEcf((ClientLevel) level, origin, pylonPos);
                        ParticleHelperInternal.spawnSpiralFE((ClientLevel) level, origin, pylonPos);
                    }
                }
            }
        }
    }

    public void renderCube(float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, Level level) {
        pPoseStack.pushPose();

        float scale = 2f;
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        pPoseStack.scale(scale, scale, scale);


        //noinspection DataFlowIssue
        this.ecfCubeModel.setupAnim(null, 0, 0, level.getGameTime() + partialTick, 0, 0);
        this.ecfCubeModel.renderToBuffer(
                pPoseStack,
                pBuffer.getBuffer(RenderType.entityTranslucent(CUBE_TEXTURE)),
                pPackedLight,
                pPackedOverlay,
                0xEAFFFFFF
        );

        pPoseStack.popPose();
    }
}

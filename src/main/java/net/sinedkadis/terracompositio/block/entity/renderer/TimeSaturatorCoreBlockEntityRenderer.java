package net.sinedkadis.terracompositio.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.block.entity.TimeSaturatorCoreBlockEntity;
import net.sinedkadis.terracompositio.entity.client.ECFCubeModel;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.registries.TCModelLayers;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sinedkadis.terracompositio.entity.client.FlowCedarEntRenderer.CUBE_TEXTURE;

@ParametersAreNonnullByDefault
public class TimeSaturatorCoreBlockEntityRenderer implements BlockEntityRenderer<TimeSaturatorCoreBlockEntity> {
    public final ECFCubeModel<FlowCedarEntEntity> ecfCubeModel;

    public TimeSaturatorCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.ecfCubeModel = new ECFCubeModel<>(context.bakeLayer(TCModelLayers.ECF_CUBE_LAYER));
    }


    @Override
    public void render(TimeSaturatorCoreBlockEntity pBlockEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        if (level == null) return;


        renderCube(partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, level, 1);


    }

    public void renderCube(float partialTick,
                           PoseStack pPoseStack,
                           MultiBufferSource pBuffer,
                           int pPackedLight,
                           int pPackedOverlay,
                           Level level,
                           float speed) {
        pPoseStack.pushPose();

        float scale = 2f;
        pPoseStack.translate(0.5D, 0.7D, 0.5D);
        pPoseStack.scale(scale, scale, scale);


        //noinspection DataFlowIssue
        this.ecfCubeModel.setupAnim(null, speed * 0.1f, 0, level.getGameTime() + partialTick, 0, 0);
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

package net.sinedkadis.terracompositio.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.sinedkadis.terracompositio.block.entity.FlowCedarAltarBlockEntity;
import org.jetbrains.annotations.NotNull;

import static net.sinedkadis.terracompositio.api.helpers.WorldHelper.getLightLevel;

public class FlowCedarAltarBlockEntityRenderer implements BlockEntityRenderer<FlowCedarAltarBlockEntity> {
    public FlowCedarAltarBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext) {

    }


    @Override
    public void render(FlowCedarAltarBlockEntity pBlockEntity,
                       float pPartialTick,
                       @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBuffer,
                       int pPackedLight,
                       int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pBlockEntity.getBlockPos(), null);
        if (handler == null) return;
        ItemStack stack1 = handler.getStackInSlot(0);
        ItemStack stack2 = handler.getStackInSlot(1);
        ItemStack stack3 = handler.getStackInSlot(2);
        int renderCount = 0;
        if (!stack1.isEmpty()) renderCount++;
        if (!stack2.isEmpty()) renderCount++;
        if (!stack3.isEmpty()) renderCount++;


        if (!stack1.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            if (renderCount > 1) {
                pPoseStack.translate(0.2f, 0, 0.2f);
            }

            pPoseStack.scale(0.5f, 0.5f, 0.5f);

            pPoseStack.mulPose(Axis.YP.rotation((level.getGameTime() + pPartialTick) / 100));
            itemRenderer.renderStatic(stack1, ItemDisplayContext.FIXED, getLightLevel(level, pBlockEntity.getBlockPos(), null),
                    OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, level, 1);
            pPoseStack.popPose();
        }

        if (!stack2.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            if (renderCount > 1) {
                pPoseStack.translate(-0.2f, 0, -0.2f);
            }

            pPoseStack.scale(0.5f, 0.5f, 0.5f);

            pPoseStack.mulPose(Axis.YP.rotation((level.getGameTime() + pPartialTick) / 100));
            itemRenderer.renderStatic(stack2, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), null),
                    OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }

        if (!stack3.isEmpty()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5f, 0.5f, 0.5f);

            pPoseStack.scale(0.5f, 0.5f, 0.5f);

            pPoseStack.mulPose(Axis.YP.rotation((level.getGameTime() + pPartialTick) / 100));
            itemRenderer.renderStatic(stack3, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), null),
                    OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
        }
    }
}

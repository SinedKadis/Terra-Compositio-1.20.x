package net.sinedkadis.terracompositio.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sinedkadis.terracompositio.block.entity.FlowCedarTankBlockEntity;
import net.sinedkadis.terracompositio.fluid.FluidRenderer;
import org.jetbrains.annotations.NotNull;

public class FlowCedarTankBlockEntityRenderer implements BlockEntityRenderer<FlowCedarTankBlockEntity> {
    public FlowCedarTankBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext) {

    }

    private static final float TANK_HEIGHT = 10 / 16f;
    private static final float TANK_BOTTOM = 3 / 16f;
    private static final float TANK_WIDTH = 10 / 16f;
    private static final float TANK_DEPTH = 10 / 16f;
    private static final float TANK_OFFSET = 3 / 16f;

    @Override
    public void render(FlowCedarTankBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        if (level == null) return;

        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pBlockEntity.getBlockPos(), null);
        if (handler == null) return;

        if (!(handler instanceof FluidTank tank)) return;
        if (tank.isEmpty()) return;

        FluidStack fluidStack = tank.getFluid();
        float fillRatio = (float) tank.getFluidAmount() / tank.getCapacity();
        float renderHeight = TANK_BOTTOM + (TANK_HEIGHT * fillRatio);

        FluidRenderer.renderFluidBox(pPoseStack, fluidStack,
                TANK_OFFSET, TANK_BOTTOM, TANK_OFFSET,
                TANK_OFFSET + TANK_WIDTH, renderHeight, TANK_OFFSET + TANK_DEPTH,
                pBuffer, pPackedLight, true);

    }
}

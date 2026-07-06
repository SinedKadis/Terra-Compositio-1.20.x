package net.sinedkadis.terracompositio.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidRenderer {
    private static final float PIXEL = 1f / 16f;

    public static void renderFluidBox(PoseStack poseStack, FluidStack fluid,
                                      float xMin, float yMin, float zMin,
                                      float xMax, float yMax, float zMax,
                                      MultiBufferSource buffer, int light, boolean renderBottom) {

        TextureAtlasSprite fluidTexture = getFluidTexture(fluid);
        int color = getFluidColor(fluid);

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        PoseStack.Pose last = poseStack.last();

        float width = xMax - xMin;
        float height = yMax - yMin;
        float depth = zMax - zMin;

        // Основные UV-координаты текстуры
        float uMin = fluidTexture.getU0();
        float uMax = fluidTexture.getU1();
        float vMin = fluidTexture.getV0();
        float vMax = fluidTexture.getV1();

        // UV для верхней грани
        float v = (uMax - uMin) * (width / PIXEL) / 16f;
        float topUMax = uMin + v;
        float topVMax = vMin + (vMax - vMin) * (depth / PIXEL) / 16f;

        // Top face (верхняя грань)
        builder.addVertex(last, xMin, yMax, zMin).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(0, 1, 0);
        builder.addVertex(last, xMin, yMax, zMax).setColor(color).setUv(uMin, topVMax).setLight(light).setNormal(0, 1, 0);
        builder.addVertex(last, xMax, yMax, zMax).setColor(color).setUv(topUMax, topVMax).setLight(light).setNormal(0, 1, 0);
        builder.addVertex(last, xMax, yMax, zMin).setColor(color).setUv(topUMax, vMin).setLight(light).setNormal(0, 1, 0);

        // Bottom face (нижняя грань)
        if (renderBottom) {
            builder.addVertex(last, xMax, yMin, zMin).setColor(color).setUv(topUMax, vMin).setLight(light).setNormal(0, -1, 0);
            builder.addVertex(last, xMax, yMin, zMax).setColor(color).setUv(topUMax, topVMax).setLight(light).setNormal(0, -1, 0);
            builder.addVertex(last, xMin, yMin, zMax).setColor(color).setUv(uMin, topVMax).setLight(light).setNormal(0, -1, 0);
            builder.addVertex(last, xMin, yMin, zMin).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(0, -1, 0);
        }

        // UV для боковых граней
        float sideUMax = uMin + v;
        float sideVMax = vMin + (vMax - vMin) * (height / PIXEL) / 16f;

        // North face (Z-) - отзеркалено, но с нормалью наружу
        builder.addVertex(last, xMax, yMin, zMin).setColor(color).setUv(uMin, sideVMax).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(last, xMin, yMin, zMin).setColor(color).setUv(sideUMax, sideVMax).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(last, xMin, yMax, zMin).setColor(color).setUv(sideUMax, vMin).setLight(light).setNormal(0, 0, -1);
        builder.addVertex(last, xMax, yMax, zMin).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(0, 0, -1);

        // South face (Z+)
        builder.addVertex(last, xMin, yMin, zMax).setColor(color).setUv(uMin, sideVMax).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(last, xMax, yMin, zMax).setColor(color).setUv(sideUMax, sideVMax).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(last, xMax, yMax, zMax).setColor(color).setUv(sideUMax, vMin).setLight(light).setNormal(0, 0, 1);
        builder.addVertex(last, xMin, yMax, zMax).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(0, 0, 1);

        // West face (X-)
        builder.addVertex(last, xMin, yMin, zMin).setColor(color).setUv(sideUMax, sideVMax).setLight(light).setNormal(-1, 0, 0);
        builder.addVertex(last, xMin, yMin, zMax).setColor(color).setUv(uMin, sideVMax).setLight(light).setNormal(-1, 0, 0);
        builder.addVertex(last, xMin, yMax, zMax).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(-1, 0, 0);
        builder.addVertex(last, xMin, yMax, zMin).setColor(color).setUv(sideUMax, vMin).setLight(light).setNormal(-1, 0, 0);

        // East face (X+)
        builder.addVertex(last, xMax, yMin, zMax).setColor(color).setUv(sideUMax, sideVMax).setLight(light).setNormal(1, 0, 0);
        builder.addVertex(last, xMax, yMin, zMin).setColor(color).setUv(uMin, sideVMax).setLight(light).setNormal(1, 0, 0);
        builder.addVertex(last, xMax, yMax, zMin).setColor(color).setUv(uMin, vMin).setLight(light).setNormal(1, 0, 0);
        builder.addVertex(last, xMax, yMax, zMax).setColor(color).setUv(sideUMax, vMin).setLight(light).setNormal(1, 0, 0);
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {
        Fluid fluidType = fluid.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidType);
        ResourceLocation stillTexture = clientFluid.getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
    }

    private static int getFluidColor(FluidStack fluid) {
        Fluid fluidType = fluid.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluidType);
        return clientFluid.getTintColor(fluid);
    }
}
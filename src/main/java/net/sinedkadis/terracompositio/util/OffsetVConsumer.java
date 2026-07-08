package net.sinedkadis.terracompositio.util;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.blaze3d.vertex.VertexConsumer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OffsetVConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float vOffset;

    public OffsetVConsumer(VertexConsumer delegate, float vOffset) {
        this.delegate = delegate;
        this.vOffset = vOffset;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return delegate.setUv(u, v + vOffset);
    }

    @Override
    public VertexConsumer setUv1(int i, int i1) {
        return delegate.setUv1(i,i1);
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        return delegate.addVertex(x, y, z);
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return delegate.setColor(r, g, b, a);
    }


    @Override
    public VertexConsumer setUv2(int u, int v) {
        return delegate.setUv2(u, v);
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return delegate.setNormal(x, y, z);
    }



}

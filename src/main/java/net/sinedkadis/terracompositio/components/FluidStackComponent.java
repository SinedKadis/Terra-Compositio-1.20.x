package net.sinedkadis.terracompositio.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidStackComponent(FluidStack fluidStack) {
    public static Codec<FluidStackComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                FluidStack.CODEC.fieldOf("fluid_stack").forGetter(FluidStackComponent::fluidStack)
        ).apply(instance,FluidStackComponent::new)
    );
    public static StreamCodec<RegistryFriendlyByteBuf,FluidStackComponent> STREAM_CODEC = StreamCodec.composite(
            FluidStack.STREAM_CODEC,FluidStackComponent::fluidStack,FluidStackComponent::new
    );
}

package net.sinedkadis.terracompositio.particle;

import lombok.Getter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

// ParticleOptions.Deserializer is gone entirely (see ECFParticleData port earlier in this thread).
// Command parsing + disk storage are both handled by the MapCodec, network sync by the
// StreamCodec - both are now defined on the ParticleType itself instead of here, since they
// need a reference to which ParticleType instance to attach the resulting FluidParticleData to
// (see TCParticles.FLUID_FLOW below). This class stays a plain data holder.
@Getter
public class FluidParticleData implements ParticleOptions {

    @NotNull
    private final ParticleType<FluidParticleData> type;
    // net.minecraftforge.fluids.FluidStack -> net.neoforged.neoforge.fluids.FluidStack.
    // The old readFromPacket/writeToPacket instance methods are gone too - FluidStack now ships
    // its own STREAM_CODEC (and CODEC) fields, used directly in TCParticles below instead.
    private final FluidStack fluidStack;

    public FluidParticleData(@NotNull ParticleType<FluidParticleData> type, FluidStack fluidStack) {
        this.type = type;
        this.fluidStack = fluidStack;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type;
    }

    // writeToNetwork(FriendlyByteBuf) and writeToString() are gone from the ParticleOptions
    // interface entirely under 1.21.1 - see TCParticles.FLUID_FLOW's codec()/streamCodec() for
    // where that logic now lives (FluidStack.CODEC / FluidStack.STREAM_CODEC handle the fluid
    // name + amount + components serialization for us, so we don't even need to touch
    // BuiltInRegistries.FLUID by hand like the old ForgeRegistries.FLUIDS.getKey(...) call did).
}
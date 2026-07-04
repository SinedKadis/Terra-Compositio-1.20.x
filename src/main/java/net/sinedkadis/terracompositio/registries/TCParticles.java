package net.sinedkadis.terracompositio.registries;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import net.sinedkadis.terracompositio.particle.FluidParticleData;
import org.jetbrains.annotations.NotNull;

public class TCParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, TerraCompositio.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLOW_PARTICLE =
            PARTICLE_TYPES.register("flow_particle",() -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, ParticleType<ECFParticleData>> ECF_PARTICLE =
            PARTICLE_TYPES.register("ecf_particle", () -> new ParticleType<>(false) {
                @Override
                public @NotNull MapCodec<ECFParticleData> codec() {
                    return ECFParticleData.CODEC;
                }

                @Override
                public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ECFParticleData> streamCodec() {
                    return ECFParticleData.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIRCH_JUICE_PARTICLE =
            PARTICLE_TYPES.register("birch_juice_particle",() -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLOW_SPLASH_PARTICLE =
            PARTICLE_TYPES.register("flow_splash_particle",() -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIRCH_JUICE_SPLASH_PARTICLE =
            PARTICLE_TYPES.register("birch_juice_splash_particle",() -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, ParticleType<FluidParticleData>> FLUID_FLOW =
            PARTICLE_TYPES.register("fluid_flow", () -> new ParticleType<>(false) {
                @Override
                public @NotNull MapCodec<FluidParticleData> codec() {
                    return FluidStack.CODEC.xmap(
                            fluid -> new FluidParticleData(this, fluid),
                            FluidParticleData::getFluidStack
                    ).fieldOf("fluid");
                }

                @Override
                public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> streamCodec() {
                    return FluidStack.STREAM_CODEC.map(
                            fluid -> new FluidParticleData(this, fluid),
                            FluidParticleData::getFluidStack
                    );
                }
            });

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

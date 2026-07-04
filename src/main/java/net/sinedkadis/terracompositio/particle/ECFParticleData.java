package net.sinedkadis.terracompositio.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sinedkadis.terracompositio.registries.TCParticles;
import org.jetbrains.annotations.NotNull;

public record ECFParticleData(float speed) implements ParticleOptions {

    // Since 1.20.5+, ParticleType#codec() returns a MapCodec, not a plain Codec, so we build it
    // with RecordCodecBuilder.mapCodec(...) instead of the old RecordCodecBuilder.create(...).
    // This single codec now covers both disk/NBT storage AND command parsing (the old separate
    // "fromCommand(StringReader)" deserializer method is gone - command args are read as
    // key=value pairs matching this codec's fields, e.g. "speed=1.0").
    public static final MapCodec<ECFParticleData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("speed").forGetter(ECFParticleData::speed)
            ).apply(instance, ECFParticleData::new)
    );

    // Network (de)serialization used to be the "fromNetwork(ParticleType, FriendlyByteBuf)" /
    // "writeToNetwork(FriendlyByteBuf)" pair on the old Deserializer/ParticleOptions. That's been
    // replaced with a StreamCodec, which is what ParticleType#streamCodec() now asks for.
    public static final StreamCodec<ByteBuf, ECFParticleData> STREAM_CODEC =
            ByteBufCodecs.FLOAT.map(ECFParticleData::new, ECFParticleData::speed);

    @Override
    public @NotNull ParticleType<?> getType() {
        return TCParticles.ECF_PARTICLE.get();
    }
}
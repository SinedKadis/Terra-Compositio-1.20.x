package net.sinedkadis.terracompositio.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record KnowledgeComponent(BlockPos pos, UUID entityUUID, CompoundTag tag) {

    public static final Codec<KnowledgeComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(KnowledgeComponent::pos),
            UUIDUtil.CODEC.fieldOf("entityUUID").forGetter(KnowledgeComponent::entityUUID),
            Codec.withAlternative(CompoundTag.CODEC, TagParser.AS_CODEC)
                    .fieldOf("tag").forGetter(KnowledgeComponent::tag)
    ).apply(inst, KnowledgeComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeComponent> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, KnowledgeComponent::pos,
            UUIDUtil.STREAM_CODEC, KnowledgeComponent::entityUUID,
            ByteBufCodecs.COMPOUND_TAG, KnowledgeComponent::tag,
            KnowledgeComponent::new
    );
}

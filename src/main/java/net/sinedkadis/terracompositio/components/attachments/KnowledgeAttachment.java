package net.sinedkadis.terracompositio.components.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KnowledgeAttachment {
    @Setter
    @Getter
    private boolean creationAcknowledged;

    public static final Codec<KnowledgeAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("knowledge").forGetter(KnowledgeAttachment::isCreationAcknowledged)
            ).apply(instance,KnowledgeAttachment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,KnowledgeAttachment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,KnowledgeAttachment::isCreationAcknowledged,KnowledgeAttachment::new
    );

    public KnowledgeAttachment(boolean creationAcknowledged) {
        this.creationAcknowledged = creationAcknowledged;
    }

    public KnowledgeAttachment() {
        setCreationAcknowledged(false);
    }
}

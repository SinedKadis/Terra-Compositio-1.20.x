package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record S2CKnowledgeInfoPayload(KnowledgeComponent knowledgeComponent) implements CustomPacketPayload {
    public static final Type<S2CKnowledgeInfoPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("knowledge_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CKnowledgeInfoPayload> STREAM_CODEC =
            StreamCodec.composite(
                    KnowledgeComponent.STREAM_CODEC, S2CKnowledgeInfoPayload::knowledgeComponent,
                    S2CKnowledgeInfoPayload::new
            );
    public static final BlockPos emptyPos = BlockPos.ZERO.atY(-64);
    public static final UUID emptyUUID = UUID.randomUUID();

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

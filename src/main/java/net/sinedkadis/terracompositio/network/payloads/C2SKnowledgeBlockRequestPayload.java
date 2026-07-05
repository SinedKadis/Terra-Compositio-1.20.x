package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sinedkadis.terracompositio.TerraCompositio;
import org.jetbrains.annotations.NotNull;

public record C2SKnowledgeBlockRequestPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<C2SKnowledgeBlockRequestPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("knowledge_block_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SKnowledgeBlockRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SKnowledgeBlockRequestPayload::blockPos,
                    C2SKnowledgeBlockRequestPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sinedkadis.terracompositio.TerraCompositio;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SKnowledgeEntityRequestPayload(UUID uuid) implements CustomPacketPayload {
    public static final Type<C2SKnowledgeEntityRequestPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("knowledge_entity_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SKnowledgeEntityRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, C2SKnowledgeEntityRequestPayload::uuid,
                    C2SKnowledgeEntityRequestPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

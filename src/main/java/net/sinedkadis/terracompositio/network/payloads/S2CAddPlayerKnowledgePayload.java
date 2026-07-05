package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.network.ClientPayloadHandlers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record S2CAddPlayerKnowledgePayload() implements CustomPacketPayload {
    public static final Type<S2CAddPlayerKnowledgePayload> TYPE =
            new Type<>(TerraCompositio.modLoc("knowledge_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CAddPlayerKnowledgePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull S2CAddPlayerKnowledgePayload decode(RegistryFriendlyByteBuf buffer) {
                    return new S2CAddPlayerKnowledgePayload();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, S2CAddPlayerKnowledgePayload value) {

                }
            };

    public static void handle(S2CAddPlayerKnowledgePayload payload) {
        ClientPayloadHandlers.handleAddPlayerKnowledgePayload();
    }

    boolean yes() {
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

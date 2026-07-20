package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.network.ClientPayloadHandlers;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record S2CHighLightNodesPayload(
        BlockPos blockPos,
        BlockPos outputPos,
        BlockPos receiverPos,
        Set<BlockPos> senderPoses,
        Set<BlockPos> inputPoses
) implements CustomPacketPayload {

    public static final Type<S2CHighLightNodesPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("highlight_nodes"));
    public static StreamCodec<RegistryFriendlyByteBuf, Set<BlockPos>> STREAM_BLOCKPOS_SET_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Set<BlockPos> decode(RegistryFriendlyByteBuf buffer) {
            Set<BlockPos> set = new HashSet<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                set.add(BlockPos.STREAM_CODEC.decode(buffer));
            }
            return set;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, Set<BlockPos> value) {
            buffer.writeVarInt(value.size());
            for (BlockPos blockPos : value) {
                BlockPos.STREAM_CODEC.encode(buffer, blockPos);
            }
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CHighLightNodesPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, S2CHighLightNodesPayload::blockPos,
                    BlockPos.STREAM_CODEC, S2CHighLightNodesPayload::outputPos,
                    BlockPos.STREAM_CODEC, S2CHighLightNodesPayload::receiverPos,
                    STREAM_BLOCKPOS_SET_CODEC, S2CHighLightNodesPayload::senderPoses,
                    STREAM_BLOCKPOS_SET_CODEC, S2CHighLightNodesPayload::inputPoses,
                    S2CHighLightNodesPayload::new
            );

    public S2CHighLightNodesPayload(PathPointerBlockEntity ppbe) {
        this(ppbe.getBlockPos(),
                ppbe.getOutputPos(),
                ppbe.getReceiverPos(),
                ppbe.getSenderPoses(),
                ppbe.getInputPoses());
    }

    public static void handle(S2CHighLightNodesPayload payload) {
        ClientPayloadHandlers.handleHighLightNodesPayload(payload);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

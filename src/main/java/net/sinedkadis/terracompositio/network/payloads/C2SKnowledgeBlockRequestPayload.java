package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import net.sinedkadis.terracompositio.registries.TCAttachments;
import org.jetbrains.annotations.NotNull;

public record C2SKnowledgeBlockRequestPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<C2SKnowledgeBlockRequestPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("knowledge_block_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SKnowledgeBlockRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SKnowledgeBlockRequestPayload::blockPos,
                    C2SKnowledgeBlockRequestPayload::new
            );

    public static void handle(ServerPlayer serverPlayer, BlockPos pos) {

        if (!serverPlayer.getData(TCAttachments.KNOWLEDGE).isCreationAcknowledged()) return;


        ServerLevel level = (serverPlayer).serverLevel();

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof IHaveKnowledge ihk)) return;

        // Проверяем дистанцию (защита от спама с дальней дистанции)
        if (serverPlayer.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) > 64 * 64) return;

        // Собираем данные на сервере
        CompoundTag data = new CompoundTag();
        ihk.collectKnowledgeData(data, level.registryAccess());

        if (data.isEmpty()) return;

        PacketDistributor.sendToPlayer(serverPlayer, new S2CKnowledgeInfoPayload(
                new KnowledgeComponent(pos, SentinelHelper.EMPTY_UUID, data)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

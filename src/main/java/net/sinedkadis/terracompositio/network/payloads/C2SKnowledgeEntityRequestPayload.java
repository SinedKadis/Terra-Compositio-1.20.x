package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import net.sinedkadis.terracompositio.util.accessors.PlayerKnowledgeAccessor;
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

    public static void handle(C2SKnowledgeEntityRequestPayload payload, IPayloadContext context) {
        ServerPlayer serverPlayer = (ServerPlayer) context.player();
        UUID uuid = payload.uuid();

        if (!((PlayerKnowledgeAccessor) serverPlayer).isCreationAcknowledged()) return;

        ServerLevel level = serverPlayer.serverLevel();

        Entity entity = level.getEntity(uuid);
        if (!(entity instanceof IHaveKnowledge ihk)) return;


        if (serverPlayer.distanceToSqr(
                entity.position().x() + 0.5,
                entity.position().y() + 0.5,
                entity.position().z() + 0.5) > 64 * 64) return;

        CompoundTag data = new CompoundTag();
        ihk.collectKnowledgeData(data, level.registryAccess());

        if (data.isEmpty()) return;
        PacketDistributor.sendToPlayer(serverPlayer, new S2CKnowledgeInfoPayload(
                new KnowledgeComponent(SentinelHelper.EMPTY_POS, uuid, data)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

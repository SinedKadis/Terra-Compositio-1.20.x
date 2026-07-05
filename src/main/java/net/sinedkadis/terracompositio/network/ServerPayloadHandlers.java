package net.sinedkadis.terracompositio.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import net.sinedkadis.terracompositio.network.payloads.S2CKnowledgeInfoPayload;
import net.sinedkadis.terracompositio.util.accessors.PlayerKnowledgeAccessor;

import java.util.UUID;

public class ServerPayloadHandlers {
    public static void handleKnowledgeBlockRequestPayload(ServerPlayer serverPlayer, BlockPos pos) {

        if (!((PlayerKnowledgeAccessor) serverPlayer).isCreationAcknowledged()) return;


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
                new KnowledgeComponent(pos, S2CKnowledgeInfoPayload.emptyUUID, data)));
    }

    public static void handleKnowledgeEntityRequestPayload(ServerPlayer player, UUID uuid) {
        if (!((PlayerKnowledgeAccessor) player).isCreationAcknowledged()) return;

        ServerLevel level = player.serverLevel();
        Entity entity = level.getEntity(uuid);
        if (!(entity instanceof IHaveKnowledge ihk)) return;


        if (player.distanceToSqr(
                entity.position().x() + 0.5,
                entity.position().y() + 0.5,
                entity.position().z() + 0.5) > 64 * 64) return;

        CompoundTag data = new CompoundTag();
        ihk.collectKnowledgeData(data, level.registryAccess());

        if (data.isEmpty()) return;
        PacketDistributor.sendToPlayer(player, new S2CKnowledgeInfoPayload(
                new KnowledgeComponent(S2CKnowledgeInfoPayload.emptyPos, uuid, data)));
    }
}

package net.sinedkadis.terracompositio.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import net.sinedkadis.terracompositio.gui.KnowledgeOverlay;
import net.sinedkadis.terracompositio.network.payloads.S2CHighLightNodesPayload;
import net.sinedkadis.terracompositio.util.accessors.PlayerKnowledgeAccessor;

import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientPayloadHandlers {
    public static void handleKnowledgeDataPayload(KnowledgeComponent pkt) {
        BlockPos pos1 = pkt.pos();
        if (!pos1.equals(SentinelHelper.EMPTY_POS))
            KnowledgeOverlay.ClientCache.put(pos1, pkt.tag());
        else {
            UUID entityUUID1 = pkt.entityUUID();
            if (!entityUUID1.equals(SentinelHelper.EMPTY_UUID)) {
                KnowledgeOverlay.ClientCache.put(entityUUID1, pkt.tag());
            }
        }
    }

    public static void handleAddPlayerKnowledgePayload() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ((PlayerKnowledgeAccessor) player).setCreationKnowledge(true);
        }
    }

    public static void handleHighLightNodesPayload(S2CHighLightNodesPayload msg) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            BlockEntity blockEntity = level.getBlockEntity(msg.blockPos());
            if (blockEntity instanceof PathPointerBlockEntity pathPointerBlockEntity) {
                pathPointerBlockEntity.setOutputPos(msg.outputPos());
                pathPointerBlockEntity.setReceiverPos(msg.receiverPos());

                Set<BlockPos> senderPoses = pathPointerBlockEntity.getSenderPoses();
                senderPoses.clear();
                senderPoses.addAll(msg.senderPoses());

                Set<BlockPos> inputPoses = pathPointerBlockEntity.getInputPoses();
                inputPoses.clear();
                inputPoses.addAll(msg.inputPoses());
            }
        }
    }
}

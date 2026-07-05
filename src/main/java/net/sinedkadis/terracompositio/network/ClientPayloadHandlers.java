package net.sinedkadis.terracompositio.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.sinedkadis.terracompositio.components.KnowledgeComponent;
import net.sinedkadis.terracompositio.network.payloads.S2CKnowledgeInfoPayload;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPayloadHandlers {


    public static void handleKnowledgeDataPayload(KnowledgeComponent pkt) {
        BlockPos pos1 = pkt.pos();
        if (!pos1.equals(S2CKnowledgeInfoPayload.emptyPos))
            ClientCache.put(pos1, pkt.tag());
        else {
            UUID entityUUID1 = pkt.entityUUID();
            if (!entityUUID1.equals(S2CKnowledgeInfoPayload.emptyUUID)) {
                ClientCache.put(entityUUID1, pkt.tag());
            }
        }
    }


    // ─── Клиентский кэш ──────────────────────────────────────────
    public static final class ClientCache {

        private static final Map<Object, CompoundTag> cache = new ConcurrentHashMap<>();


        public static void put(BlockPos pos, CompoundTag data) {
            cache.put(pos, data);
        }

        public static void put(UUID entityUUID, CompoundTag data) {
            cache.put(entityUUID, data);
        }


        public static CompoundTag get(Object posOrUUID) {
            return cache.get(posOrUUID);
        }


        public static void clear() {
            cache.clear();
        }
    }
}

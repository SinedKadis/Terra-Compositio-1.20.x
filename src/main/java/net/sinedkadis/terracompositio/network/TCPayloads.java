package net.sinedkadis.terracompositio.network;


import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.network.payloads.C2SKnowledgeBlockRequestPayload;
import net.sinedkadis.terracompositio.network.payloads.C2SKnowledgeEntityRequestPayload;
import net.sinedkadis.terracompositio.network.payloads.S2CKnowledgeInfoPayload;

public class TCPayloads {


    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TerraCompositioAPI.MOD_ID).versioned("1");

        registrar.playToClient(
                S2CKnowledgeInfoPayload.TYPE,
                S2CKnowledgeInfoPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> ClientPayloadHandlers.handleKnowledgeDataPayload(payload.knowledgeComponent())
                )
        );
        registrar.playToServer(
                C2SKnowledgeBlockRequestPayload.TYPE,
                C2SKnowledgeBlockRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPayloadHandlers.handleKnowledgeBlockRequestPayload(((ServerPlayer) context.player()), payload.blockPos());
                })
        );
        registrar.playToServer(
                C2SKnowledgeEntityRequestPayload.TYPE,
                C2SKnowledgeEntityRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPayloadHandlers.handleKnowledgeEntityRequestPayload(((ServerPlayer) context.player()), payload.uuid());
                })
        );

    }


}

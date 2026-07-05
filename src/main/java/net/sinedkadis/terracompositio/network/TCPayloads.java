package net.sinedkadis.terracompositio.network;


import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.network.payloads.*;

@EventBusSubscriber(modid = TerraCompositioAPI.MOD_ID)
public class TCPayloads {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TerraCompositioAPI.MOD_ID).versioned("1");

        registrar.playToClient(
                S2CKnowledgeInfoPayload.TYPE,
                S2CKnowledgeInfoPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> S2CKnowledgeInfoPayload.handle(payload)
                )
        );
        registrar.playToServer(
                C2SKnowledgeBlockRequestPayload.TYPE,
                C2SKnowledgeBlockRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        C2SKnowledgeBlockRequestPayload.handle(((ServerPlayer) context.player()), payload.blockPos()))
        );
        registrar.playToServer(
                C2SKnowledgeEntityRequestPayload.TYPE,
                C2SKnowledgeEntityRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        C2SKnowledgeEntityRequestPayload.handle(payload, context))
        );
        registrar.playToServer(
                C2SBoardSyncPayload.TYPE,
                C2SBoardSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        C2SBoardSyncPayload.handle(payload, context))
        );
        registrar.playToClient(
                S2CAddPlayerKnowledgePayload.TYPE,
                S2CAddPlayerKnowledgePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        S2CAddPlayerKnowledgePayload.handle(payload))
        );
        registrar.playToClient(
                S2CHighLightNodesPayload.TYPE,
                S2CHighLightNodesPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        S2CHighLightNodesPayload.handle(payload))
        );

    }


}

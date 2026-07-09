package net.sinedkadis.terracompositio.registries;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.components.attachments.KnowledgeAttachment;

public class TCAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TerraCompositio.MOD_ID);


    public static final DeferredHolder<AttachmentType<?>,AttachmentType<KnowledgeAttachment>> KNOWLEDGE =
            ATTACHMENTS.register("knowledge", () -> AttachmentType
                    .builder(() -> new KnowledgeAttachment())
                    .serialize(KnowledgeAttachment.CODEC)
                    .sync(KnowledgeAttachment.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus eventBus){
        ATTACHMENTS.register(eventBus);
    }

}

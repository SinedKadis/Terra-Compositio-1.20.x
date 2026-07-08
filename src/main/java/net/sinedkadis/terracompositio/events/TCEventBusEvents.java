package net.sinedkadis.terracompositio.events;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.registries.TCEntities;
import net.sinedkadis.terracompositio.particle.custom.*;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class TCEventBusEvents {


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(TCEntities.FLOW_CEDAR_ENT.get(), FlowCedarEntEntity.createAttributes().build());
    }
}

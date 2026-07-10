package net.sinedkadis.terracompositio.events;


import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.registries.TCEntities;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class TCEventBusEvents {


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(TCEntities.FLOW_CEDAR_ENT.get(), FlowCedarEntEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(TCEntities.FLOW_CEDAR_ENT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlowCedarEntEntity::checkEntSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.AND);
    }

}

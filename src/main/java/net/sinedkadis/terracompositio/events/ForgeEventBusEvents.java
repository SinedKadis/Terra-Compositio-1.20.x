package net.sinedkadis.terracompositio.events;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.config.TCServerConfigs;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.item.custom.KnowledgeAppleItem;
import net.sinedkadis.terracompositio.item.custom.TechnetiumArmorItem;
import net.sinedkadis.terracompositio.registries.TCFluids;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class ForgeEventBusEvents {
    @SubscribeEvent
    public static void onLivingHurt(EntityInvulnerabilityCheckEvent event) {
        TechnetiumArmorItem.onLivingHurtEvent(event);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        TCFluids.applyLiquidFlowEffect(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        FlowCedarEntEntity.onEntityInteractEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        KnowledgeAppleItem.onPlayerClonedEvent(event);
    }

    @SubscribeEvent
    public static void onTickLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.getGameTime() % TCServerConfigs.LAZY_UPDATE_RATE.get() == 0) {
            TerraCompositioAPI.instance().getECFNetworkInstance().updateAll(level);
        }


    }


}
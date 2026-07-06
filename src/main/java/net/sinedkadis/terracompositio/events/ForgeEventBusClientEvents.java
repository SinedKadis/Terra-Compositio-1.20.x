package net.sinedkadis.terracompositio.events;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.gui.KnowledgeOverlay;
import net.sinedkadis.terracompositio.item.custom.FlowBottleItem;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID,value = Dist.CLIENT)
public class ForgeEventBusClientEvents {

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            KnowledgeOverlay.ClientCache.clear();
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        WrenchAxeItem.onRenderHandEvent(event);
    }


    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity().level() instanceof ClientLevel clientLevel) {
            FlowBottleItem.onClientLivingTickEvent(event, clientLevel);
            WrenchAxeItem.onClientLevelTickEndEvent(event);
        }
    }

}

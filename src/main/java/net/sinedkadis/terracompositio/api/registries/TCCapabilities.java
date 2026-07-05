package net.sinedkadis.terracompositio.api.registries;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;


/**
 * The Forge Caps, used in my mod.
 */
@EventBusSubscriber(modid = TerraCompositioAPI.MOD_ID)
public class TCCapabilities {
    public static final Capability<IECFHandler> ECF = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IItemHandlerModifiable> ITEM_STATE_HOLDER = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IECFHandler.class);
    }
}

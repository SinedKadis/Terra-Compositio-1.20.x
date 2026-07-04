package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;

public class TCPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, TerraCompositio.MOD_ID);

    //public static final RegistryObject<Potion> FLOW_BOTTLE = POTIONS.register("flow_bottle",
    //        () -> new FlowBottleItem(new Item.Properties()))

    public static void register(IEventBus eventBus){
        POTIONS.register(eventBus);
    }
}

package net.sinedkadis.terracompositio.potion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.item.custom.FlowBottleItem;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, TerraCompositio.MOD_ID);

    //public static final RegistryObject<Potion> FLOW_BOTTLE = POTIONS.register("flow_bottle",
    //        () -> new FlowBottleItem(new Item.Properties()))

    public static void register(IEventBus eventBus){
        POTIONS.register(eventBus);
    }
}

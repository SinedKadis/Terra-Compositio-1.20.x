package net.sinedkadis.terracompositio.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TerraCompositio.MODID);

    public static final RegistryObject<Item> Pebble = ITEMS.register("pebble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> StoneStaff = ITEMS.register("stone_staff",
            () -> new Item(new Item.Properties()));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}

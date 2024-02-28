package net.sinedkadis.terracompositio.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TerraCompositio.MOD_ID);

    public static final RegistryObject<Item> PEBBLE = ITEMS.register("pebble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_STAFF = ITEMS.register("stone_staff",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}

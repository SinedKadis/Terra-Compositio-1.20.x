package net.sinedkadis.terracompositio.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> Creative_mode_tabs =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraCompositio.MODID);
    public static final RegistryObject<CreativeModeTab> Terra_Compositio = Creative_mode_tabs.register("terra_compositio",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.Pebble.get()))
                    .title(Component.translatable("creativetab.terra_compositio"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.Pebble.get());
                        pOutput.accept(ModItems.StoneStaff.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        Creative_mode_tabs.register(eventBus);
    }
}

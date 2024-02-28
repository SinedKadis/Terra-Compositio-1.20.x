package net.sinedkadis.terracompositio.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> Creative_mode_tabs =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraCompositio.MOD_ID);
    public static final RegistryObject<CreativeModeTab> Terra_Compositio = Creative_mode_tabs.register("terra_compositio",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.PEBBLE.get()))
                    .title(Component.translatable("creativetab.terra_compositio"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.PEBBLE.get());
                        pOutput.accept(ModItems.STONE_STAFF.get());
                        pOutput.accept(ModBlocks.FLOW_LOG.get());
                        pOutput.accept(ModBlocks.FLOW_WOOD.get());
                        pOutput.accept(ModBlocks.NONFLOW_LOG.get());
                        pOutput.accept(ModBlocks.NONFLOW_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_NONFLOW_LOG.get());
                        pOutput.accept(ModBlocks.STRIPPED_NONFLOW_WOOD.get());
                        pOutput.accept(ModBlocks.FLOW_PORT.get());
                        pOutput.accept(ModBlocks.NONFLOW_PLANKS.get());
                        pOutput.accept(ModBlocks.FLOW_LEAVES.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        Creative_mode_tabs.register(eventBus);
    }
}

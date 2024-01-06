package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.custom.FlowLog;
import net.sinedkadis.terracompositio.item.ModItems;

import java.util.function.Supplier;

import static net.minecraft.world.item.Items.registerBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TerraCompositio.MODID);

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static final RegistryObject<Block> FLOW_LOG = registerBlock("flow_log",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> FLOW_WOOD = registerBlock("flow_wood",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_FLOW_LOG = registerBlock("stripped_flow_log",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_FLOW_WOOD = registerBlock("stripped_flow_wood",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));

    public static final RegistryObject<Block> FLOW_PLANKS = registerBlock("flow_planks",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(3f)));
    public static final RegistryObject<Block> FLOW_LEAVES = registerBlock("flow_leaves",
            () -> new FlowLog(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
}

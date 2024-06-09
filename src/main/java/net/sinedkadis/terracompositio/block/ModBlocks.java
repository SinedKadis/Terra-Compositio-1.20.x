package net.sinedkadis.terracompositio.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.custom.*;
import net.sinedkadis.terracompositio.item.ModItems;
import net.sinedkadis.terracompositio.sound.ModSounds;

import java.util.function.Supplier;



public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TerraCompositio.MOD_ID);

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block>void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static final RegistryObject<Block> FLOW_LOG = registerBlock("flow_log",
            () -> new FlowLogLikeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f).sound(ModSounds.FLOW_LOG_LIKE_BLOCK_SOUNDS)));
    public static final RegistryObject<Block> FLOW_PORT = registerBlock("flow_log_port",
            () -> new FlowWoodPortBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f).sound(ModSounds.FLOW_LOG_LIKE_BLOCK_SOUNDS)));
    public static final RegistryObject<Block> NONFLOW_PORT = registerBlock("nonflow_log_port",
            () -> new NonFlowLikeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));

    public static final RegistryObject<Block> FLOW_WOOD = registerBlock("flow_wood",
            () -> new FlowLogLikeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(3f).sound(ModSounds.FLOW_LOG_LIKE_BLOCK_SOUNDS)));
    public static final RegistryObject<Block> STRIPPED_NONFLOW_LOG = registerBlock("stripped_nonflow_log",
            () -> new StrippedFlowLogLikeBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_NONFLOW_WOOD = registerBlock("stripped_nonflow_wood",
            () -> new StrippedFlowLogLikeBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    public static final RegistryObject<Block> NONFLOW_LOG = registerBlock("nonflow_log",
            () -> new NonFlowLikeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> NONFLOW_WOOD = registerBlock("nonflow_wood",
            () -> new NonFlowLikeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3f)));

    public static final RegistryObject<Block> NONFLOW_PLANKS = registerBlock("nonflow_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)){
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final RegistryObject<Block> FLOW_LEAVES = registerBlock("flow_leaves",
            () -> new FlowLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noLootTable()));
    public static final RegistryObject<Block> NONFLOW_STAIRS = registerBlock("nonflow_stairs",
            () -> new StairBlock(() -> ModBlocks.NONFLOW_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NONFLOW_SLAB = registerBlock("nonflow_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(3f)));
    public static final RegistryObject<Block> NONFLOW_BUTTON = registerBlock("nonflow_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS),
                    BlockSetType.OAK,30,true));
    public static final RegistryObject<Block> NONFLOW_PRESSURE_PLATE = registerBlock("nonflow_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS),BlockSetType.OAK));
    public static final RegistryObject<Block> NONFLOW_FENCE = registerBlock("nonflow_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NONFLOW_FENCE_GATE = registerBlock("nonflow_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), SoundEvents.FENCE_GATE_OPEN,SoundEvents.FENCE_GATE_CLOSE));
    public static final RegistryObject<Block> NONFLOW_DOOR = registerBlock("nonflow_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<Block> NONFLOW_TRAPDOOR = registerBlock("nonflow_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(),BlockSetType.OAK));

    public static final RegistryObject<Block> FLOW_CAULDRON = registerBlock("flow_cauldron",
            () -> new FlowCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), null,CauldronInteraction.EMPTY));
    public static final RegistryObject<Block> BIRCH_JUICE_CAULDRON = registerBlock("birch_juice_cauldron",
            () -> new FlowCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), null,CauldronInteraction.EMPTY));
    public static final RegistryObject<Block> WEDGE = registerBlock("wedge",
            () -> new WedgeBlock(BlockBehaviour.Properties.copy(Blocks.TRIPWIRE_HOOK)));
}

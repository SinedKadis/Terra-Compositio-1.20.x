package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.custom.*;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.item.custom.UnstableTechnetiumBlockItem;
import net.sinedkadis.terracompositio.worldgen.TCConfiguredFeatures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Supplier;


@ParametersAreNonnullByDefault
public class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TerraCompositio.MOD_ID);
    public static final DeferredBlock<Block> FLOW_CEDAR_ALTAR = registerBlock("flow_cedar_altar",
            () -> new FlowCedarAltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(3f)));
    public static final DeferredBlock<Block> STRIPPED_FLOW_CEDAR_LOG = registerBlock("stripped_flow_cedar_log",
            () -> new FlowCedarLikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    //Cedar blocks
    public static final DeferredBlock<Block> FLOW_CEDAR_LOG = registerBlock("flow_cedar_log",
            () -> new FlowCedarLikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).strength(3f), TCBlocks.STRIPPED_FLOW_CEDAR_LOG));
    public static final DeferredBlock<Block> STRIPPED_FLOW_CEDAR_WOOD = registerBlock("stripped_flow_cedar_wood",
            () -> new FlowCedarLikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    public static final DeferredBlock<Block> FLOW_CEDAR_WOOD = registerBlock("flow_cedar_wood",
            () -> new FlowCedarLikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(3f), TCBlocks.STRIPPED_FLOW_CEDAR_WOOD));
    public static final DeferredBlock<Block> FLOW_CEDAR_PLANKS = registerBlock("flow_cedar_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)) {
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
    public static final DeferredBlock<Block> FLOW_CEDAR_STAIRS = registerBlock("flow_cedar_stairs",
            () -> new StairBlock(TCBlocks.FLOW_CEDAR_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> FLOW_CEDAR_LEAVES = registerBlock("flow_cedar_leaves",
            () -> new FlowCedarLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> FLOW_CEDAR_SLAB = registerBlock("flow_cedar_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(3f)));
    public static final DeferredBlock<Block> FLOW_CEDAR_BUTTON = registerBlock("flow_cedar_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> FLOW_CEDAR_PRESSURE_PLATE = registerBlock("flow_cedar_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> FLOW_CEDAR_FENCE = registerBlock("flow_cedar_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> FLOW_CEDAR_FENCE_GATE = registerBlock("flow_cedar_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE));
    public static final DeferredBlock<Block> FLOW_CEDAR_DOOR = registerBlock("flow_cedar_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final DeferredBlock<Block> FLOW_CEDAR_TRAPDOOR = registerBlock("flow_cedar_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final DeferredBlock<Block> FLOW_CEDAR_SIGN = BLOCKS.register("flow_cedar_sign",
            () -> new TCStandingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN), TCWoodTypes.FLOW_CEDAR));
    public static final DeferredBlock<Block> FLOW_CEDAR_WALL_SIGN = BLOCKS.register("flow_cedar_wall_sign",
            () -> new TCWallSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN), TCWoodTypes.FLOW_CEDAR));
    public static final DeferredBlock<Block> FLOW_CEDAR_HANGING_SIGN = BLOCKS.register("flow_cedar_hanging_sign",
            () -> new TCHangingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN), TCWoodTypes.FLOW_CEDAR));
    public static final DeferredBlock<Block> FLOW_CEDAR_WALL_HANGING_SIGN = BLOCKS.register("flow_cedar_wall_hanging_sign",
            () -> new TCWallHangingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), TCWoodTypes.FLOW_CEDAR));
    public static final DeferredBlock<Block> FLOW_INFUSER = registerBlock("flow_infuser",
            () -> new FlowInfuserBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final DeferredBlock<Block> FLOW_CEDAR_SAPLING = registerBlock("flow_cedar_sapling",
            () -> new FlowCedarSaplingBlock(new TreeGrower(
                    "flow_cedar",
                    Optional.empty(),
                    Optional.of(TCConfiguredFeatures.FLOW_CEDAR_KEY),
                    Optional.empty()
            ), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));




    //Cauldron related
    public static final DeferredBlock<Block> FLOW_CAULDRON = registerBlock("flow_cauldron",
            () -> new FlowCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), Biome.Precipitation.NONE, CauldronInteraction.EMPTY));
    public static final DeferredBlock<Block> BIRCH_JUICE_CAULDRON = registerBlock("birch_juice_cauldron",
            () -> new BirchJuiceCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), Biome.Precipitation.NONE, CauldronInteraction.EMPTY));
    public static final DeferredBlock<Block> WEDGE = registerBlock("wedge",
            () -> new WedgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK)));


    //Creative
    public static final DeferredBlock<Block> CREATIVE_ECF_SOURCE = registerBlock("creative_ecf_source",
            () -> new CreativeECFSourceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> ECF_TRASH_CAN = registerBlock("ecf_trash_can",
            () -> new ECFTrashCanBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));


    //Technetium
    public static final DeferredBlock<Block> TECHNETIUM_ORE = registerUnstableTechnetiumBlock("technetium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)), 2);
    public static final DeferredBlock<Block> TECHNETIUM_DEEPSLATE_ORE = registerUnstableTechnetiumBlock("technetium_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)), 2);
    public static final DeferredBlock<Block> TECHNETIUM_RAW_ORE_BLOCK = registerUnstableTechnetiumBlock("technetium_raw_ore_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)), 8);
    public static final DeferredBlock<Block> TECHNETIUM_BLOCK = registerBlock("technetium_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK)));


    //Infused Iron
    public static final DeferredBlock<Block> INFUSED_IRON_BLOCK = registerBlock("infused_iron_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));


    //Matter infuser
    public static final DeferredBlock<Block> FLOW_CEDAR_CASING = registerBlock("flow_cedar_casing",
            () -> new FlowCedarCasingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final DeferredBlock<Block> MATTER_INFUSER_PORT = registerBlock("matter_infuser_port",
            () -> new MatterInfuserPortBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK).sound(SoundType.COPPER).strength(3f)));
    public static final DeferredBlock<Block> MATTER_INFUSER_UNIT = registerBlock("matter_infuser_unit",
            () -> new MatterInfuserUnitBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRIPWIRE_HOOK).sound(SoundType.COPPER).strength(3f)));


    //Desorbers
    public static final DeferredBlock<Block> CONSTRUCTION_DESORBER = registerBlock("construction_desorber",
            () -> new ConstructionDesorberBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3f).noOcclusion()));
    public static final DeferredBlock<Block> CULTIVATION_DESORBER = registerBlock("cultivation_desorber",
            () -> new CultivationDesorberBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3f).noOcclusion()));
    public static final DeferredBlock<Block> TIME_PASSAGE_DESORBER = registerBlock("time_passage_desorber",
            () -> new TimePassageDesorberBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3f).noOcclusion()));


    //Cedar tanks
    public static final DeferredBlock<Block> FLOW_CEDAR_PEDESTAL = registerBlock("flow_cedar_pedestal",
            () -> new FlowCedarPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AZALEA).noOcclusion()));
    public static final DeferredBlock<Block> FLOW_CEDAR_TANK = registerBlock("flow_cedar_tank",
            () -> new FlowCedarTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final DeferredBlock<Block> FLOW_CEDAR_TANK_2 = registerBlock("flow_cedar_tank_2",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion().noLootTable()));
    public static final DeferredBlock<Block> FLOW_CEDAR_TANK_3 = registerBlock("flow_cedar_tank_3",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion().noLootTable()));


    //Path pointers
    public static final DeferredBlock<Block> PP_RECEIVER = registerBlock("pp_receiver",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.RECEIVER));
    public static final DeferredBlock<Block> PP_COLLECTOR = registerBlock("pp_collector",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.COLLECTOR));
    public static final DeferredBlock<Block> PP_SENDER = registerBlock("pp_sender",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.SENDER));
    public static final DeferredBlock<Block> PP_EMITTER = registerBlock("pp_emitter",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.EMITTER));
    public static final DeferredBlock<Block> PP_EXTRACTOR = registerBlock("pp_extractor",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.EXTRACTOR));
    public static final DeferredBlock<Block> PP_INFUSER = registerBlock("pp_infuser",
            () -> new PathPointerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion(), PathPointerBlockEntity.PPPart.INFUSER));


    //Infused Iron Based redstone
    public static final DeferredBlock<Block> FLOATING_REDSTONE = registerBlock("floating_redstone",
            () -> new RedStoneWireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE)) {
                @Override
                public boolean canSurviveOn(BlockGetter level, BlockPos pos, BlockState state) {
                    return true;
                }
            });
    public static final DeferredBlock<Block> FLOATING_REPEATER = registerBlock("floating_repeater",
            () -> new RepeaterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)) {
                @Override
                public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {

                    Direction facing = state.getValue(RepeaterBlock.FACING);
                    return facing == direction || facing.getOpposite() == direction;
                }

                @Override
                protected boolean canSurviveOn(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }
            });
    public static final DeferredBlock<Block> FLOATING_COMPARATOR = registerBlock("floating_comparator",
            () -> new FloatingComparatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COMPARATOR)));
    public static final DeferredBlock<Block> FLOATING_TORCH_HOLDER = registerBlock("floating_torch_holder",
            () -> new FloatingTorchHolderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));
    public static final DeferredBlock<Block> INFUSED_IRON_PRESSURE_PLATE = registerBlock("infused_iron_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)) {
                @Override
                public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
                    return true;
                }
                @Override
                public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
                    return true;
                }
            });
    public static final DeferredBlock<Block> INFUSED_IRON_DOOR = registerBlock("infused_iron_door",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR)) {
                @Override
                public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
                    BlockPos blockpos = pPos.below();
                    BlockState blockstate = pLevel.getBlockState(blockpos);
                    return pState.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
                }

                @Override
                public @NotNull BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
                    DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
                    if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                        BlockPos blockpos = pPos.below();
                        BlockState blockstate = pLevel.getBlockState(blockpos);
                        if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                            BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                            pLevel.setBlock(blockpos, blockstate1, 35);
                            pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
                        }
                    } else {
                        BlockPos blockpos = pPos.above();
                        BlockState blockstate = pLevel.getBlockState(blockpos);
                        if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.UPPER) {
                            BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                            pLevel.setBlock(blockpos, blockstate1, 35);
                            pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
                        }
                    }
                    super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
                    return pState;
                }
            });
    public static final DeferredBlock<Block> FLOATING_BUTTON = registerBlock("floating_button",
            () -> new ButtonBlock(BlockSetType.IRON, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)) {
                @Override
                public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
                    return true;
                }

                @Override
                public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
                    return true;
                }
            });
    public static final DeferredBlock<Block> FLOATING_LEVER = registerBlock("floating_lever",
            () -> new LeverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)) {
                @Override
                public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
                    return true;
                }

                @Override
                public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
                    return true;
                }
            });

    //Misc
    public static final DeferredBlock<Block> FLOW_CEDAR_ENT_STATUE = registerBlock("flow_cedar_ent_statue",
            () -> new EntStatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> ECF_BOARD = registerBlock("ecf_board",
            () -> new ECFBoardBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).sound(new DeferredSoundType(1.0F,
                    1.0F,
                    () -> SoundEvents.AMETHYST_BLOCK_STEP,
                    () -> SoundEvents.AMETHYST_BLOCK_STEP,
                    () -> SoundEvents.AMETHYST_BLOCK_PLACE,
                    () -> SoundEvents.AMETHYST_BLOCK_HIT,
                    () -> SoundEvents.AMETHYST_BLOCK_FALL)).noLootTable().noOcclusion()));
    public static final DeferredBlock<Block> AIR_SATURATOR = registerBlock("air_saturator",
            () -> new AirSaturatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(3f)));


    //Formal Energy Provider
    public static final DeferredBlock<Block> FE_PROVIDER_CORE = registerBlock("fe_provider_core",
            () -> new FEProviderCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).noOcclusion()));
    public static final DeferredBlock<Block> FE_PROVIDER_PYLON = registerBlock("fe_provider_pylon",
            () -> new FEProviderPylonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TINTED_GLASS).noOcclusion()));

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, () -> true);
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Supplier<Boolean> predicate) {
        if (!predicate.get()) return null;
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        TCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> DeferredBlock<T> registerUnstableTechnetiumBlock(String name, Supplier<T> block, int radiation) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerUnstableTechnetiumBlockItem(name, toReturn,radiation);
        return toReturn;
    }

    private static <T extends Block> void registerUnstableTechnetiumBlockItem(String name, DeferredBlock<T> block, int radiation) {
        TCItems.ITEMS.register(name, () -> new UnstableTechnetiumBlockItem(block.get(), new Item.Properties(),radiation));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}

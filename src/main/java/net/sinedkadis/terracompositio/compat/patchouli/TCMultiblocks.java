package net.sinedkadis.terracompositio.compat.patchouli;

import com.google.common.base.Suppliers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.custom.FlowCedarTankBlock;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCFluids;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.TriPredicate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TCMultiblocks {
    public static final Supplier<IMultiblock> MATTER_INFUSER_MB = Suppliers.memoize(() -> {

        var casing = TCBlocks.FLOW_CEDAR_CASING.get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X)
                .setValue(TCBlockStateProperties.INFUSED,true);
        var io = TCBlocks.MATTER_INFUSER_UNIT.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING,Direction.get(Direction.AxisDirection.POSITIVE,Direction.Axis.Z));
        var port = TCBlocks.MATTER_INFUSER_PORT.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING,Direction.get(Direction.AxisDirection.POSITIVE,Direction.Axis.Z));

        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                            "C0",
                            "CI",
                            "CI"
                        }
                },
                'C', casing,
                'I', io,
                '0', port
        );
    });
    public static final Supplier<IMultiblock> CREATION_ALTAR_MB = Suppliers.memoize(() -> {

        var altar = TCBlocks.FLOW_CEDAR_ALTAR.get().defaultBlockState()
                .setValue(TCBlockStateProperties.INFUSED, true);
        var pedestal = TCBlocks.FLOW_CEDAR_PEDESTAL.get().defaultBlockState();
        var mud = Blocks.MUD_BRICKS.defaultBlockState();
        BlockState stairs = Blocks.MUD_BRICK_STAIRS.defaultBlockState();
        BlockState[] mud_stairs = {
                stairs.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                stairs.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                stairs.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                stairs.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
        };
        var moss = Blocks.MOSS_BLOCK.defaultBlockState();

        return PatchouliAPI.get().makeMultiblock(
                new String[][]{
                        {
                                "   ",
                                " a ",
                                "   "
                        },
                        {
                                "   ",
                                " p ",
                                "   "
                        },
                        {
                                "MeM",
                                "s0n",
                                "MwM"
                        }
                },
                'M', mud,
                'n', mud_stairs[0],
                'e', mud_stairs[1],
                's', mud_stairs[2],
                'w', mud_stairs[3],
                'p', pedestal,
                'a', altar,
                '0', moss
        ).setSymmetrical(true);
    });

    public static final Supplier<IMultiblock> TIME_SATURATOR_MB = Suppliers.memoize(() -> {

        var core = TCBlocks.TIME_SATURATOR_CORE.get().defaultBlockState();
        var technetium = TCBlocks.TECHNETIUM_BLOCK.get().defaultBlockState();
        var desorber = TCBlocks.TIME_PASSAGE_DESORBER.get().defaultBlockState();
        var flow = new FlowContainingState();

        return PatchouliAPI.get().makeMultiblock(
                new String[][]{
                        {
                                "   ",
                                " C ",
                                "   "
                        },
                        {
                                "   ",
                                " t ",
                                "   "
                        },
                        {
                                " d ",
                                "dtd",
                                " d "
                        },
                        {
                                " f ",
                                "f0f",
                                " f "
                        }
                },
                'd', desorber,
                'C', core,
                '0', technetium,
                't', technetium,
                'f', flow
        ).setSymmetrical(true);
    });

    private static class FlowContainingState implements IStateMatcher {

        private final BlockState LOG = TCBlocks.FLOW_CEDAR_LOG.get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.Y)
                .setValue(TCBlockStateProperties.INFUSED, true);
        private final BlockState WOOD = TCBlocks.FLOW_CEDAR_WOOD.get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.Y)
                .setValue(TCBlockStateProperties.INFUSED, true);
        private final BlockState CASING = TCBlocks.FLOW_CEDAR_CASING.get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.Y)
                .setValue(TCBlockStateProperties.INFUSED, true);
        private final BlockState TANK = TCBlocks.FLOW_CEDAR_TANK.get().defaultBlockState()
                .setValue(FlowCedarTankBlock.STAGE, 3);
        private final BlockState FLOW = TCFluids.FLOW_FLUID.block.get().defaultBlockState();

        @Override
        public BlockState getDisplayedState(long ticks) {
            long x = ticks % 80;
            if (x < 20) {
                return LOG;
            }
            if (x < 40) {
                return WOOD;
            }
            if (x < 60) {
                return CASING;
            }
//            if (x < 80) {
//                return TANK;
//            }

            return FLOW;
        }

        @Override
        public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {

            return ((blockGetter, pos, blockState) -> {
                boolean exceptions = blockState.equals(TANK) || blockState.equals(FLOW);
                return exceptions || (blockState.hasProperty(TCBlockStateProperties.INFUSED) && blockState.getValue(TCBlockStateProperties.INFUSED));
            });
        }
    }
}
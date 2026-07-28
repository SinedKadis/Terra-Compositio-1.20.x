package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.block.entity.FEProviderCoreBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderCoreBlock extends TCBaseEntityBlock implements IFluidApplicable {
    public FEProviderCoreBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(TCBlockStateProperties.INFUSED, false));
    }

    @Override
    public BlockEntityType<? extends TCBlockEntity> getBlockEntityType() {
        return TCBlockEntities.FE_PROVIDER_CORE_BE.get();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement == null) return null;
        return stateForPlacement.setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, TCBlockStateProperties.INFUSED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FEProviderCoreBlockEntity fepBE) {
            BlockPos.betweenClosedStream(pos.offset(-7, 0, -7), pos.offset(7, 0, 7))
                    .filter(blockPos -> !blockPos.closerThan(pos, 1.5f))
                    .filter(blockPos -> {
                        BlockState blockState = level.getBlockState(blockPos);
                        return blockState.is(TCBlocks.FE_PROVIDER_PYLON.get())
                                && blockState.getValue(TCBlockStateProperties.INFUSED);
                    })
                    .forEach(blockPos -> {
                        fepBE.getPylonPoses().add(blockPos.immutable());
                        fepBE.setChanged();
                        level.sendBlockUpdated(fepBE.getBlockPos(), fepBE.getBlockState(), fepBE.getBlockState(), 3);
                    });
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (!pNewState.getBlock().equals(pState.getBlock())) {
            WorldHelperInternal.flowLeak(pState, pLevel, pPos);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FEProviderCoreBlockEntity fepBE) {
            ITCRecipe.CraftException craftException = ITCRecipe.checkSurroundings(fepBE);
            if (craftException.hasExceptions()) {
                fepBE.exception = craftException;
            }
            fepBE.exceptionLock = craftException.hasExceptions();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}

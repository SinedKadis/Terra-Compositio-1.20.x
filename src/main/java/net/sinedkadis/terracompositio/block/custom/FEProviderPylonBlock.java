package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.block.entity.FEProviderCoreBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderPylonBlock extends Block implements IFluidApplicable {
    public FEProviderPylonBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(TCBlockStateProperties.INFUSED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TCBlockStateProperties.INFUSED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.join(
                Block.box(3, 0, 3, 13, 2, 13),
                Shapes.join(Block.box(5, 2, 5, 11, 12, 11),
                        Block.box(7, 12, 7, 9, 16, 9),
                        BooleanOp.OR),
                BooleanOp.OR);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (state.getValue(TCBlockStateProperties.INFUSED))
            BlockPos.betweenClosedStream(pos.offset(-6, 0, -6), pos.offset(6, 0, 6))
                    .filter(blockPos -> !blockPos.closerThan(pos, 1.5f))
                    .map(level::getBlockEntity)
                    .filter(FEProviderCoreBlockEntity.class::isInstance)
                    .map(FEProviderCoreBlockEntity.class::cast)
                    .forEach(be -> {
                        be.getPylonPoses().add(pos);
                        be.setChanged();
                        level.sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, level, pos, pNewState, pIsMoving);
        BlockPos.betweenClosedStream(pos.offset(-6, 0, -6), pos.offset(6, 0, 6))
                .filter(blockPos -> !blockPos.closerThan(pos, 1.5f))
                .map(level::getBlockEntity)
                .filter(FEProviderCoreBlockEntity.class::isInstance)
                .map(FEProviderCoreBlockEntity.class::cast)
                .forEach(be -> {
                    be.getPylonPoses().remove(pos);
                    be.setChanged();
                    level.sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                });
    }

    @Override
    public int defaultConsumeAmount() {
        return 500;
    }
}

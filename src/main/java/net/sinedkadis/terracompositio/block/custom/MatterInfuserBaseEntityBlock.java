package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity;
import net.sinedkadis.terracompositio.block.entity.MatterInfuserUnitBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@SuppressWarnings("deprecation")
public abstract class MatterInfuserBaseEntityBlock extends TCBaseEntityBlock {
    protected final static DirectionProperty FACING;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;

    public MatterInfuserBaseEntityBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.EAST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (blockstate.hasProperty(AXIS) && blockstate.is(TCBlocks.FLOW_CEDAR_CASING.get())) {
            Direction.Axis axis = blockstate.getValue(AXIS);
            return axis.isHorizontal() && !axis.equals(direction.getAxis());
        }
        return false;
    }



    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        InteractionResult use = super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        if (!use.equals(InteractionResult.SUCCESS)) {
            Direction direction = pState.getValue(FACING);

            BlockPos behindPos = pPos.relative(direction.getOpposite());
            BlockState behindState = pLevel.getBlockState(behindPos);
            InteractionResult behindUse = behindState.use(pLevel, pPlayer, pHand, pHit.withPosition(behindPos));
            if (behindUse.equals(InteractionResult.SUCCESS))
                return InteractionResult.SUCCESS;

            BlockPos rightPos = pPos.relative(direction.getCounterClockWise());
            BlockState rightState = pLevel.getBlockState(rightPos);
            InteractionResult rightUse = InteractionResult.PASS;
            if (rightState.getBlock() instanceof MatterInfuserBaseEntityBlock)
                rightUse = rightState.use(pLevel, pPlayer, pHand, pHit.withPosition(rightPos));
            if (rightUse.equals(InteractionResult.SUCCESS))
                return InteractionResult.SUCCESS;
        }
        return use;
    }

    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        if (direction == Direction.SOUTH) {
            return SOUTH_AABB;
        } else if (direction == Direction.WEST) {
            return WEST_AABB;
        } else if (direction == Direction.NORTH) {
            return NORTH_AABB;
        }
        return EAST_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState();
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Direction[] adirection = pContext.getNearestLookingDirections();
        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(level, blockpos)) {
                    BlockPos relative = blockpos.relative(direction1.getOpposite(), 2);
                    BlockState state = level.getBlockState(relative);
                    if (!(state.getBlock() instanceof MatterInfuserBaseEntityBlock
                            && state.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(direction1.getOpposite())))
                        return blockstate;
                }
            }
        }

        return null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        Direction direction = pState.getValue(HORIZONTAL_FACING);
        BlockPos blockPos2 = pPos.relative(direction.getCounterClockWise());
        BlockEntity blockEntity1;
        blockEntity1 = pLevel.getBlockEntity(blockPos2);
        if (blockEntity1 instanceof MatterInfuserUnitBlockEntity) {
            ItemHelper.dropContents(blockEntity1, TCCapabilities.ITEM_STATE_HOLDER);
        }
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        BlockState blockState = pLevel.getBlockState(blockpos);
        if (blockState.is(TCBlocks.FLOW_CEDAR_CASING.get())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(blockpos);
            if (blockEntity instanceof FlowCedarCasingBlockEntity) {
                ItemHelper.dropContents(blockEntity, TCCapabilities.ITEM_STATE_HOLDER,
                        FlowCedarCasingBlockEntity.UP_CONNECTION_SLOT,
                        FlowCedarCasingBlockEntity.DOWN_CONNECTION_SLOT);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING,pRotation.rotate(pState.getValue(FACING)));
    }

    static {
         FACING = BlockStateProperties.HORIZONTAL_FACING;
         SOUTH_AABB = Block.box(3, 3, 0, 13, 13, 1);
         NORTH_AABB = Block.box(3, 3, 15, 13, 13, 16);
         EAST_AABB = Block.box(0, 3, 3, 1, 13, 13);
         WEST_AABB = Block.box(15, 3, 3, 16, 13, 13);
    }
}

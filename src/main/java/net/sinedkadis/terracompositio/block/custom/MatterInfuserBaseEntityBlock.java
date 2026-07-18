package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCCapabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

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
        if (blockstate.hasProperty(AXIS) && blockstate.is(TCBlocks.FLOW_CEDAR_CASING.get()))
            return direction.getAxis().isHorizontal() && !blockstate.getValue(AXIS).equals(direction.getAxis());
        return false;
    }



    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult use = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (!use.equals(ItemInteractionResult.SUCCESS)) {
            Direction direction = state.getValue(FACING);

            BlockPos behindPos = pos.relative(direction.getOpposite());
            BlockState behindState = level.getBlockState(behindPos);
            ItemInteractionResult behindUse = behindState.useItemOn(stack, level, player, hand, hitResult.withPosition(behindPos));
            if (behindUse.equals(ItemInteractionResult.SUCCESS))
                return ItemInteractionResult.SUCCESS;

            BlockPos rightPos = pos.relative(direction.getCounterClockWise());
            BlockState rightState = level.getBlockState(rightPos);
            ItemInteractionResult rightUse = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (rightState.getBlock() instanceof MatterInfuserBaseEntityBlock)
                rightUse = rightState.useItemOn(stack, level, player, hand, hitResult.withPosition(rightPos));
            if (rightUse.equals(ItemInteractionResult.SUCCESS))
                return ItemInteractionResult.SUCCESS;
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
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING,pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        Direction direction = pState.getValue(HORIZONTAL_FACING);
        BlockPos leftPos = pPos.relative(direction.getCounterClockWise());
        BlockState leftState = pLevel.getBlockState(leftPos);
        if (leftState.is(TCBlocks.MATTER_INFUSER_UNIT)) {
            ItemHelper.dropContents(pLevel, leftPos, TCCapabilities.ITEM_STATE_HOLDER_BLOCK);
        }
        BlockPos backPos = pPos.relative(direction.getOpposite());
        BlockState backState = pLevel.getBlockState(backPos);
        if (backState.is(TCBlocks.FLOW_CEDAR_CASING.get())) {
            ItemHelper.dropContents(pLevel, backPos, TCCapabilities.ITEM_STATE_HOLDER_BLOCK);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    static {
         FACING = BlockStateProperties.HORIZONTAL_FACING;
         SOUTH_AABB = Block.box(3, 3, 0, 13, 13, 1);
         NORTH_AABB = Block.box(3, 3, 15, 13, 13, 16);
         EAST_AABB = Block.box(0, 3, 3, 1, 13, 13);
         WEST_AABB = Block.box(15, 3, 3, 16, 13, 13);
    }
}

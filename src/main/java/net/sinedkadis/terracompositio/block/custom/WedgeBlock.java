package net.sinedkadis.terracompositio.block.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sinedkadis.terracompositio.block.ModBlocks;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

public class WedgeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final VoxelShape NORTH_AABB = Block.box(6.0D, 5.0D, 10.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(6.0D, 5.0D, 0.0D, 10.0D, 10.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.box(10.0D, 5.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 5.0D, 6.0D, 6.0D, 10.0D, 10.0D);

    private static final Logger LOGGER = LogUtils.getLogger();

    public WedgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            default -> EAST_AABB;
            case WEST -> WEST_AABB;
            case SOUTH -> SOUTH_AABB;
            case NORTH -> NORTH_AABB;
        };
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(pLevel, blockpos, direction);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState().setValue(ATTACHED, Boolean.FALSE);
        LevelReader levelreader = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Direction[] adirection = pContext.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.calculateState(pState, pLevel, pPos,true);
        double random = Math.random();
        if (pState.getValue(ATTACHED)) {
            if (pLevel.getBlockState(pPos.below()).hasProperty(LEVEL)) {
                //LOGGER.debug("Flow Cauldron detected, trying increase level");
                int levelValue = pLevel.getBlockState(pPos.below()).getValue(LEVEL);
                if (random < 0.9D) {
                    if (levelValue != 3) {
                        boolean success = pLevel.setBlock(pPos.below(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, levelValue + 1), 2);
                        //LOGGER.debug(random + " - Success " + success);
                    } //else LOGGER.debug(random + " - Fail");
                }
            } else if (pLevel.getBlockState(pPos.below()) == Blocks.CAULDRON.defaultBlockState()) {
                //LOGGER.debug("Vanilla Cauldron detected, trying increase level");
                if (random < 0.9D) {
                    boolean success = pLevel.setBlock(pPos.below(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, 1), 2);
                    //LOGGER.debug(random + " - Success " + success);
                } //else LOGGER.debug(random + " - Fail");
            }
        }

    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @org.jetbrains.annotations.Nullable LivingEntity pPlacer, ItemStack pStack) {
        this.calculateState(pState,pLevel,pPos,false);
    }

    public void calculateState(BlockState pState, Level pLevel, BlockPos pPos, boolean pShouldNotifyNeighbours) {
        switch (pState.getValue(FACING)){
            case EAST ->
            {
                if ((pLevel.getBlockState(pPos.west()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.west()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())
                        && !pState.getValue(ATTACHED)){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.EAST).setValue(ATTACHED,true),1);
                    if (pShouldNotifyNeighbours){
                        this.notifyNeighbors(pLevel,pPos,Direction.DOWN);
                    }
                }else if (pState.getValue(ATTACHED)
                        && !(pLevel.getBlockState(pPos.west()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.west()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.EAST).setValue(ATTACHED,false),1);
                }
            }
            case WEST ->
            {
                if ((pLevel.getBlockState(pPos.east()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.east()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())
                        && !pState.getValue(ATTACHED)){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.WEST).setValue(ATTACHED,true),1);
                    if (pShouldNotifyNeighbours){
                        this.notifyNeighbors(pLevel,pPos,Direction.DOWN);
                    }
                }else if (pState.getValue(ATTACHED)
                        && !(pLevel.getBlockState(pPos.east()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.east()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.WEST).setValue(ATTACHED,false),1);
                }
            }
            case NORTH ->
            {
                if ((pLevel.getBlockState(pPos.south()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.south()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())
                        && !pState.getValue(ATTACHED)){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.NORTH).setValue(ATTACHED,true),1);
                    if (pShouldNotifyNeighbours){
                        this.notifyNeighbors(pLevel,pPos,Direction.DOWN);
                    }
                }else if (pState.getValue(ATTACHED)
                        && !(pLevel.getBlockState(pPos.south()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.south()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.NORTH).setValue(ATTACHED,false),1);
                }
            }
            case SOUTH ->
            {
                if ((pLevel.getBlockState(pPos.north()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.north()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())
                        && !pState.getValue(ATTACHED)){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.SOUTH).setValue(ATTACHED,true),1);
                    if (pShouldNotifyNeighbours){
                        this.notifyNeighbors(pLevel,pPos,Direction.DOWN);
                    }
                }else if (pState.getValue(ATTACHED)
                        && !(pLevel.getBlockState(pPos.north()) == ModBlocks.FLOW_LOG.get().defaultBlockState()
                        || pLevel.getBlockState(pPos.north()) == ModBlocks.FLOW_WOOD.get().defaultBlockState())){
                    pLevel.setBlock(pPos,this.defaultBlockState().setValue(FACING,Direction.SOUTH).setValue(ATTACHED,false),1);
                }
            }

        }
    }
    private void notifyNeighbors(Level pLevel, BlockPos pPos, Direction pDirection) {
        pLevel.updateNeighborsAt(pPos, this);
        pLevel.updateNeighborsAt(pPos.relative(pDirection), this);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, ATTACHED);
    }
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pIsMoving && !pState.is(pNewState.getBlock())) {
            if (pState.getValue(ATTACHED)) {
                this.calculateState(pState,pLevel, pPos, false);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
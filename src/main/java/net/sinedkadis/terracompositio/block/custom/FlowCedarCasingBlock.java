package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.registries.TCBlocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarCasingBlock extends TCBaseEntityBlock implements IFluidApplicable {
    public static final BooleanProperty INFUSED = TCBlockStateProperties.INFUSED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    protected static final BooleanProperty WAXED = TCBlockStateProperties.WAXED;

    public FlowCedarCasingBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(INFUSED, false)
                .setValue(WAXED, false)
                .setValue(AXIS, Direction.Axis.Y));
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.getItem() instanceof BlockItem blockItem) {

            BlockPlaceContext context = new BlockPlaceContext(
                    player,
                    hand,
                    item,
                    hitResult
            );

            if (canPlace(context, blockItem.getBlock().defaultBlockState())) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        if (item.is(Items.HONEYCOMB) && !state.getValue(WAXED)) {
            return WorldHelper.handleInWorldBlockCraft(state, state.setValue(WAXED, true), level, pos, item, 1, ParticleTypes.WAX_ON, SoundEvents.HONEYCOMB_WAX_ON);
        }
        if (item.getItem() instanceof AxeItem && state.getValue(WAXED)) {
            ItemHelper.hurtAndBreakItem((ServerLevel) level, player, item);
            return WorldHelper.handleInWorldBlockCraft(state, state.setValue(WAXED, false), level, pos, item, 0, ParticleTypes.WAX_OFF, SoundEvents.AXE_WAX_OFF);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (state.canSurvive(context.getLevel(), context.getClickedPos()))
                && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext);
    }

    //returns null if no port, true if clockwise, false if counterclockwise
    public static @Nullable Boolean isBlockAttached(Level level, BlockState blockState, BlockPos pos, Block... blocks) {
        Direction.Axis axis = blockState.getValue(AXIS);
        if (axis.isVertical()) return null;

        for (Block block : blocks) {
            if (level.getBlockState(pos.relative(Direction.get(Direction.AxisDirection.POSITIVE, axis).getClockWise()))
                    .is(block))
                return true;
            if (level.getBlockState(pos.relative(Direction.get(Direction.AxisDirection.POSITIVE, axis).getCounterClockWise()))
                    .is(block))
                return false;
        }

        return null;
    }

    public static @Nullable Boolean isPortAttached(Level level, BlockState blockState, BlockPos pos) {
        return isBlockAttached(level, blockState, pos, TCBlocks.MATTER_INFUSER_PORT.get());
    }

    @SuppressWarnings("unused")
    public static @Nullable Boolean isUnitAttached(Level level, BlockState blockState, BlockPos pos) {
        return isBlockAttached(level, blockState, pos, TCBlocks.MATTER_INFUSER_UNIT.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS, INFUSED, WAXED);
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(AXIS, pContext.getClickedFace().getAxis());
    }

    @Override
    public BlockEntityType<? extends TCBlockEntity> getBlockEntityType() {
        return TCBlockEntities.FLOW_CEDAR_CASING_BE.get();
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {


        if (!pState.getBlock().equals(pNewState.getBlock())) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos relativePos = pPos.relative(direction);
                BlockState dirState = pLevel.getBlockState(relativePos);
                if (!(dirState.getBlock() instanceof MatterInfuserBaseEntityBlock)) continue;
                if (!dirState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(direction)) continue;
                pLevel.destroyBlock(relativePos, true);
            }
            WorldHelper.flowLeak(pState, pLevel, pPos);
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(AXIS,pRotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE,pState.getValue(AXIS))).getAxis());
    }
}

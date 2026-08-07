package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.registries.TCTags;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal.handleInWorldBlockCraft;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarLikeBlock extends RotatedPillarBlock implements IFluidApplicable {
    public static final BooleanProperty INFUSED;
    @Nullable
    private final Supplier<Block> stripPair;
    protected static final BooleanProperty WAXED;

    public FlowCedarLikeBlock(Properties pProperties, @Nullable Supplier<Block> stripPair) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(INFUSED, false).setValue(WAXED, false));
        this.stripPair = stripPair;
    }

    public FlowCedarLikeBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(INFUSED, false).setValue(WAXED, false));
        this.stripPair = null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS, INFUSED, WAXED);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return !state.getValue(INFUSED);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(INFUSED) ? 0 : 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(INFUSED) ? 0 : 5;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (context.getItemInHand().getItem() instanceof AxeItem && stripPair != null) {
            return stripPair.get().defaultBlockState()
                    .setValue(AXIS, state.getValue(AXIS))
                    .setValue(INFUSED, state.getValue(INFUSED));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState,
                                 Level pLevel,
                                 BlockPos pPos,
                                 Player pPlayer,
                                 InteractionHand pHand,
                                 BlockHitResult pHit) {
        ItemStack item = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack item2 = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
        if (this.getClass() == FlowCedarLikeBlock.class) {
            if (item.is(TCItems.GOLD_ROD.get())
                    && item.getCount() >= 4
                    && (item2.is(TCTags.Items.WRENCHES) || item2.is(TCItems.WRENCH_AXE.get()))) {
                if (!item2.is(TCItems.WRENCH_AXE.get()) || WrenchAxeItem.getWrenchMode(item2).equals(WrenchAxeItem.WrenchMode.WRENCH)) {
                    return handleInWorldBlockCraft(pState, TCBlocks.FLOW_CEDAR_CASING.get().defaultBlockState(), pLevel, pPos, item, 4);
                }
                return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
            } else if (item.is(TCItems.FLOW_INFUSER_KIT.get())
                    && item2.is(ItemTags.AXES)) {
                return handleInWorldBlockCraft(pState, TCBlocks.FLOW_INFUSER.get().defaultBlockState(), pLevel, pPos, item, 1);
            }
        }
        if (item.is(Items.HONEYCOMB) && !pState.getValue(WAXED)) {
            return WorldHelper.handleInWorldBlockCraft(pState, pState.setValue(WAXED, true), pLevel, pPos, item, 1, ParticleTypes.WAX_ON, SoundEvents.HONEYCOMB_WAX_ON);
        }
        if (item.getItem() instanceof AxeItem && pState.getValue(WAXED)) {
            item.hurtAndBreak(1, pPlayer, player1 -> player1.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            return WorldHelper.handleInWorldBlockCraft(pState, pState.setValue(WAXED, false), pLevel, pPos, item, 0, ParticleTypes.WAX_OFF, SoundEvents.AXE_WAX_OFF);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (pState.getBlock() != pNewState.getBlock() && WorldHelper.onRemoveHandlerBlacklist(pNewState,
                Blocks.STRUCTURE_VOID,
                TCBlocks.FLOW_CEDAR_CASING.get())) {
            WorldHelperInternal.flowLeak(pState, pLevel, pPos);
        }
    }


    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(INFUSED)) {
            for (BlockPos blockPos : BlockPos.betweenClosed(pPos.offset(-1, -1, -1), pPos.offset(1, 1, 1))) {
                if (!blockPos.equals(pPos)) {
                    if (pLevel.getBlockState(blockPos).hasProperty(INFUSED)) {
                        if (!pLevel.getBlockState(blockPos).getValue(INFUSED) && pRandom.nextFloat() > 0.99f)
                            pLevel.setBlockAndUpdate(blockPos, pLevel.getBlockState(blockPos).setValue(INFUSED, true));
                    }
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    static {
        WAXED = TCBlockStateProperties.WAXED;
        INFUSED = TCBlockStateProperties.INFUSED;
    }
}

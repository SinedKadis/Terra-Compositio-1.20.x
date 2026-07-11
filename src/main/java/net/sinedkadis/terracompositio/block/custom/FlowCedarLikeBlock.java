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
import net.minecraft.world.ItemInteractionResult;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.registries.TCTags;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static net.sinedkadis.terracompositio.api.helpers.WorldHelper.handleInWorldBlockCraft;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarLikeBlock extends RotatedPillarBlock implements IFluidApplicable {
    public static final BooleanProperty INFUSED;
    @Nullable
    private final Supplier<Block> stripPair;
    protected static final BooleanProperty WAXED;
    public FlowCedarLikeBlock(Properties pProperties, @Nullable Supplier<Block> stripPair) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(INFUSED, false).setValue(WAXED,false));
        this.stripPair = stripPair;
    }
    public FlowCedarLikeBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(INFUSED, false).setValue(WAXED,false));
        this.stripPair = null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS,INFUSED,WAXED);
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
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {

        if (ItemAbilities.AXE_STRIP == itemAbility && stripPair != null) {
            return stripPair.get().defaultBlockState()
                    .setValue(AXIS, state.getValue(AXIS))
                    .setValue(INFUSED,state.getValue(INFUSED));
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack item2 = player.getItemInHand(InteractionHand.OFF_HAND);
        if (this.getClass() == FlowCedarLikeBlock.class) {
            if (item.is(TCItems.GOLD_ROD.get())
                    && item.getCount() >= 4
                    && (item2.is(TCTags.Items.WRENCHES) || item2.is(TCItems.WRENCH_AXE.get()))) {
                if (!item2.is(TCItems.WRENCH_AXE.get()) || WrenchAxeItem.getWrenchMode(item2).equals(WrenchAxeItem.WrenchMode.WRENCH)) {
                    return handleInWorldBlockCraft(state, TCBlocks.FLOW_CEDAR_CASING.get().defaultBlockState(), level, pos, item, 4);
                }
                return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
            } else if (item.is(TCItems.FLOW_INFUSER_KIT.get())
                    && item2.is(ItemTags.AXES)) {
                return handleInWorldBlockCraft(state, TCBlocks.FLOW_INFUSER.get().defaultBlockState(), level, pos, item, 1);
            }
        }
        if (item.is(Items.HONEYCOMB) && !state.getValue(WAXED)) {
            return handleInWorldBlockCraft(state, state.setValue(WAXED, true), level, pos, item, 1, ParticleTypes.WAX_ON, SoundEvents.HONEYCOMB_WAX_ON);
        }
        if (item.getItem() instanceof AxeItem && state.getValue(WAXED)) {
            ItemHelper.hurtAndBreakItem((ServerLevel) level, player, item);
            return handleInWorldBlockCraft(state, state.setValue(WAXED, false), level, pos, item, 0, ParticleTypes.WAX_OFF, SoundEvents.AXE_WAX_OFF);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (pState.getBlock() != pNewState.getBlock() && WorldHelper.onRemoveHandlerBlacklist(pNewState,
                Blocks.STRUCTURE_VOID,
                TCBlocks.FLOW_CEDAR_CASING.get())) {
            WorldHelper.flowLeak(pState, pLevel, pPos);
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

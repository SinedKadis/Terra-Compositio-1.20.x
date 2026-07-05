package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlowCedarPedestalBlock extends Block {
    public FlowCedarPedestalBlock(Properties pProperties) {
        super(pProperties);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        return pState.getBlock() == this
                ? pLevel.getBlockState(blockpos).canSustainPlant(pLevel, blockpos, Direction.UP, pState).isTrue()
                : pState.is(BlockTags.DIRT) || pState.is(Blocks.FARMLAND);
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(Items.BONE_MEAL)) {
            BlockState blockState = level.getBlockState(pos.above());
            if (blockState.is(TCBlocks.FLOW_CEDAR_TANK.get()) && blockState.getValue(FlowCedarTankBlock.STAGE).equals(3)) {
                level.setBlockAndUpdate(pos, TCBlocks.FLOW_CEDAR_PEDESTAL.get().defaultBlockState());
                level.setBlockAndUpdate(pos.above(), blockState.setValue(FlowCedarTankBlock.STAGE, 4));
                itemInHand.shrink(1);
                FlowCedarSaplingBlock.spawnFertilizeParticles(level, pos, 10);
                FlowCedarSaplingBlock.playFertilizeSound(level, pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        BlockState blockState = pLevel.getBlockState(pPos.above());
        if (!pState.equals(pNewState) && blockState.hasProperty(FlowCedarTankBlock.STAGE)) {
            pLevel.setBlockAndUpdate(pPos.above(), blockState.setValue(FlowCedarTankBlock.STAGE, 3));
        }
    }
}

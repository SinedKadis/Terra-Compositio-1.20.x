package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.sound.ModSounds;
import net.sinedkadis.terracompositio.util.ModGameRules;
import org.jetbrains.annotations.Nullable;

public class FlowLogLikeBlock extends RotatedPillarBlock {
    public FlowLogLikeBlock(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem){
            if(state.is(ModBlocks.FLOW_LOG.get())){
                return ModBlocks.STRIPPED_NONFLOW_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(ModBlocks.FLOW_WOOD.get())){
                return ModBlocks.STRIPPED_NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context,toolAction,simulate);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.getGameRules().getBoolean(ModGameRules.DISABLE_FLOW_LEAKING)) {
            if (pLevel.getBlockState(pPos.north()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.north(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.north()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.south()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.south(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.south()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.east()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.east(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.east()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.west()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.west(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.west()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.above()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.above(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.above()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.below()).is(ModBlocks.FLOW_LOG.get())) {
                pLevel.setBlock(pPos.below(), ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.below()).getValue(AXIS)), 2);
            }



            if (pLevel.getBlockState(pPos.north()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.north(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.north()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.south()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.south(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.south()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.east()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.east(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.east()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.west()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.west(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.west()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.above()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.above(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.above()).getValue(AXIS)), 2);
            }
            if (pLevel.getBlockState(pPos.below()).is(ModBlocks.FLOW_WOOD.get())) {
                pLevel.setBlock(pPos.below(), ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS,pLevel.getBlockState(pPos.below()).getValue(AXIS)), 2);
            }


            if (pLevel.getBlockState(pPos.north()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.north(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }
            if (pLevel.getBlockState(pPos.south()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.south(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }
            if (pLevel.getBlockState(pPos.east()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.east(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }
            if (pLevel.getBlockState(pPos.west()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.west(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }
            if (pLevel.getBlockState(pPos.above()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.above(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }
            if (pLevel.getBlockState(pPos.below()).is(ModBlocks.FLOW_PORT.get())) {
                pLevel.setBlock(pPos.below(), ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
            }

            //pLevel.playSeededSound(null,pPos.getX(),pPos.getY(),pPos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS,0.1f,1.5f,0);
        }

    }
}

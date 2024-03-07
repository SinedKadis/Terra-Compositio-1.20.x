package net.sinedkadis.terracompositio.block.custom;


import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.block.ModBlocks;
import org.jetbrains.annotations.Nullable;



public class FlowWoodPortBlock extends StripableRotatedPillarBlock {
    public FlowWoodPortBlock(Properties pProperties) {
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
            if(state.is(ModBlocks.FLOW_PORT.get())){
                return ModBlocks.STRIPPED_NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,state.getValue(AXIS));
            }
            if(state.is(ModBlocks.NONFLOW_PORT.get())){
                return ModBlocks.STRIPPED_NONFLOW_LOG.get().defaultBlockState().setValue(AXIS,state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context,toolAction,simulate);
    }


    /*public (UseOnContext context) {
        if (context.getItemInHand().getItem().equals(Items.BOOK)){
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.FAIL;
        }
    }*/
}
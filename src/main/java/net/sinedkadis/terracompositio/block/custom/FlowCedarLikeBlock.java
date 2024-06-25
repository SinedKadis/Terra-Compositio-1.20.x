package net.sinedkadis.terracompositio.block.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.block.ModBlocks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static net.sinedkadis.terracompositio.util.ILikeNeighbours.CountNeighbours;
import static net.sinedkadis.terracompositio.util.ILikeNeighbours.HasNeighbour;

public class FlowCedarLikeBlock extends RotatedPillarBlock {
    public FlowCedarLikeBlock(Properties pProperties) {
        super(pProperties);
    }
    private static final Logger LOGGER = LogUtils.getLogger();
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }


    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem){
            if(state.is(ModBlocks.FLOW_CEDAR_LOG.get())){
                return ModBlocks.STRIPPED_FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(ModBlocks.FLOW_CEDAR_WOOD.get())){
                return ModBlocks.STRIPPED_FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context,toolAction,simulate);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        Block[] pBlocks = new Block[]{ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(),ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get(),ModBlocks.FLOWING_FLOW_PORT.get()};
        double number = CountNeighbours(pLevel, pPos, pBlocks);
        if (HasNeighbour(pLevel, ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(), pPos,new Direction[]{})
                || HasNeighbour(pLevel, ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get(), pPos,new Direction[]{})
                || HasNeighbour(pLevel, ModBlocks.FLOWING_FLOW_PORT.get(), pPos,new Direction[]{})){
            //LOGGER.debug("Trying to replace");
            double random = (Math.random()*100)+(1-Math.pow(1-0.1d,number-1))*50;
            if (random>97){
                //LOGGER.debug("Success "+random);
                if (pState.is(ModBlocks.FLOW_CEDAR_LOG.get())){
                    pLevel.setBlock(pPos,ModBlocks.FLOWING_FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS,pState.getValue(AXIS)),2);
                }
                if (pState.is(ModBlocks.FLOW_CEDAR_WOOD.get())){
                    pLevel.setBlock(pPos,ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS,pState.getValue(AXIS)),2);
                }
                if (pState.is(ModBlocks.FLOW_PORT.get())){
                    pLevel.setBlock(pPos,ModBlocks.FLOWING_FLOW_PORT.get().defaultBlockState().setValue(AXIS,pState.getValue(AXIS)),2);
                }
            }//else LOGGER.debug("Fail " + random);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
}

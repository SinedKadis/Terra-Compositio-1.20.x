package net.sinedkadis.terracompositio.block.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.sound.ModSounds;
import net.sinedkadis.terracompositio.util.ModGameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

public class FlowLogLikeBlock extends RotatedPillarBlock {
    public FlowLogLikeBlock(Properties pProperties) {
        super(pProperties);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

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
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (pNewState.is(Blocks.AIR)) {
            if (!pLevel.getGameRules().getBoolean(ModGameRules.DISABLE_FLOW_LEAKING)) {
                BlockPos[] LeakRange = new BlockPos[125];
                //BlockPos offset = new BlockPos(pPos.below(2).north(2).west(2));
                int i =0;
                for (int y = -2;y<=2;y++){
                    for (int z = -2;z<=2;z++){
                        for (int x = -2;x<=2;x++){
                            LeakRange[i++] = new BlockPos(pPos.getX()+x,pPos.getY()+y,pPos.getZ()+z);
                        }
                    }
                }
                Integer[] IndexBlackList = new Integer[61];
                int ind = 0;
                for (int b = 1;b<=5;b++){
                    IndexBlackList[ind++] = b;
                    IndexBlackList[ind++] = b+20;
                    IndexBlackList[ind++] = b+100;
                    IndexBlackList[ind++] = b+120;
                }
                for (int v = 0;v<5;v++){
                    IndexBlackList[ind++] = 1+5*v;
                    IndexBlackList[ind++] = 5*(v+1);
                    IndexBlackList[ind++] = (1+5*v)+100;
                    IndexBlackList[ind++] = (5*(v+1))+100;
                }
                for (int c = 0;c<5;c++){
                    IndexBlackList[ind++] = 1+25*c;
                    IndexBlackList[ind++] = 5+25*c;
                    IndexBlackList[ind++] = 21+25*c;
                    IndexBlackList[ind++] = 25+25*c;
                }
                IndexBlackList[ind++] = 63;


                for (int k = 1;k<=125;k++){
                    if (!AnyEquals(IndexBlackList,k)) {
                        LOGGER.debug("Index "+k+" is not blacklisted");
                        if (LeakRange[k] != pPos.above()
                                ||LeakRange[k] != pPos.below()
                                ||LeakRange[k] != pPos.west()
                                ||LeakRange[k] != pPos.east()
                                ||LeakRange[k] != pPos.north()
                                ||LeakRange[k] != pPos.south()) {
                            LOGGER.debug("Index "+k+" is not nearby source");
                            if (HasNeighbour(pLevel, ModBlocks.FLOW_LOG.get(), LeakRange[k])
                                    || HasNeighbour(pLevel, ModBlocks.FLOW_WOOD.get(), LeakRange[k])
                                    || HasNeighbour(pLevel, ModBlocks.FLOW_PORT.get(), LeakRange[k])) {
                                BlockPos blockPos = LeakRange[k];
                                LOGGER.debug("Log like is detected on index "+k+"("+ blockPos +"), replacing");
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_LOG.get())) {
                                    LOGGER.debug("It`s log");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)), 2);
                                }
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_WOOD.get())) {
                                    LOGGER.debug("It`s wood");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)), 2);
                                }
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                                    LOGGER.debug("It`s port");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
                                }
                            }
                        }else {
                            LOGGER.debug("Index "+k+" is nearby source");
                            BlockPos blockPos = LeakRange[k];
                            if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_LOG.get())) {
                                LOGGER.debug("It`s log");
                                pLevel.setBlock(blockPos, ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)), 2);
                            }
                            if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_WOOD.get())) {
                                LOGGER.debug("It`s wood");
                                pLevel.setBlock(blockPos, ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)), 2);
                            }
                            if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                                LOGGER.debug("It`s port");
                                pLevel.setBlock(blockPos, ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
                            }
                        }
                    }
                }


                //pLevel.playSeededSound(null,pPos.getX(),pPos.getY(),pPos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS,0.1f,1.5f,0);
            }
        }

    }
    public static boolean HasNeighbour(Level pLevel,Block pBlock,BlockPos pPos){
        return (pLevel.getBlockState(pPos.above()).is(pBlock) ||HasNeighbour(pLevel, pBlock, pPos.above(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.below()).is(pBlock)||HasNeighbour(pLevel, pBlock, pPos.below(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.north()).is(pBlock)||HasNeighbour(pLevel, pBlock, pPos.north(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.south()).is(pBlock)||HasNeighbour(pLevel, pBlock, pPos.south(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.east()).is(pBlock)||HasNeighbour(pLevel, pBlock, pPos.east(), new Direction[]{Direction.EAST, Direction.WEST}))
                || (pLevel.getBlockState(pPos.west()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.west(), new Direction[]{Direction.EAST, Direction.WEST}));
    }
    public static boolean HasNeighbour(Level pLevel,Block pBlock,BlockPos pPos,Direction[] blackListedSides){
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        if (!AnyEquals(blackListedSides,Direction.UP)) {
            flag1 = pLevel.getBlockState(pPos.above()).is(pBlock);
        }else flag1 = false;
        if (!AnyEquals(blackListedSides,Direction.DOWN)) {
            flag2 = pLevel.getBlockState(pPos.below()).is(pBlock);
        }else flag2 = false;
        if (!AnyEquals(blackListedSides,Direction.NORTH)) {
            flag3 = pLevel.getBlockState(pPos.north()).is(pBlock);
        }else flag3 = false;
        if (!AnyEquals(blackListedSides,Direction.SOUTH)) {
            flag4 = pLevel.getBlockState(pPos.south()).is(pBlock);
        }else flag4 = false;
        if (!AnyEquals(blackListedSides,Direction.EAST)) {
            flag5 = pLevel.getBlockState(pPos.east()).is(pBlock);
        }else flag5 = false;
        if (!AnyEquals(blackListedSides,Direction.WEST)) {
            flag6 = pLevel.getBlockState(pPos.west()).is(pBlock);
        }else flag6 = false;
        return flag1||flag2||flag3||flag4||flag5||flag6;
    }

    public static boolean AnyEquals(Integer[] massive,int number){
        for (Integer integer : massive) {
            if (integer == number) {
                return true;
            }
        }
        return false;
    }
    public static boolean AnyEquals(Direction[] massive,Direction direction){
        for (Direction direction1 : massive) {
            if (direction1 == direction) {
                return true;
            }
        }
        return false;
    }
}

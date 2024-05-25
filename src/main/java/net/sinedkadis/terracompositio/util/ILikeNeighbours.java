package net.sinedkadis.terracompositio.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface ILikeNeighbours {
    Logger LOGGER = LogUtils.getLogger();
    static int CountNeighbours(Level pLevel, BlockPos pPos, Block pBlock){
        int count = 0;
        if (pLevel.getBlockState(pPos.above()).is(pBlock)) count++;
        if (pLevel.getBlockState(pPos.below()).is(pBlock)) count++;
        if (pLevel.getBlockState(pPos.west()).is(pBlock)) count++;
        if (pLevel.getBlockState(pPos.east()).is(pBlock)) count++;
        if (pLevel.getBlockState(pPos.north()).is(pBlock)) count++;
        if (pLevel.getBlockState(pPos.south()).is(pBlock)) count++;
        return count;
    }
    static int CountNeighbours(Level pLevel, BlockPos pPos, Block[] pBlock){
        int count = 0;
        for (Block block : pBlock) {
            if (pLevel.getBlockState(pPos.above()).is(block)) count++;
            if (pLevel.getBlockState(pPos.below()).is(block)) count++;
            if (pLevel.getBlockState(pPos.west()).is(block)) count++;
            if (pLevel.getBlockState(pPos.east()).is(block)) count++;
            if (pLevel.getBlockState(pPos.north()).is(block)) count++;
            if (pLevel.getBlockState(pPos.south()).is(block)) count++;
        }
        return count;
    }
    static boolean HasNeighbour(Level pLevel, Block pBlock, BlockPos pPos, Direction[] blackListedSides){
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
    static boolean HasNeighbour(Level pLevel, Block pBlock, BlockPos pPos, BlockPos[] blackListedBlockPos){
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        boolean flag6;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag1 = pLevel.getBlockState(pPos.above()).is(pBlock);
        }else flag1 = false;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag2 = pLevel.getBlockState(pPos.below()).is(pBlock);
        }else flag2 = false;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag3 = pLevel.getBlockState(pPos.north()).is(pBlock);
        }else flag3 = false;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag4 = pLevel.getBlockState(pPos.south()).is(pBlock);
        }else flag4 = false;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag5 = pLevel.getBlockState(pPos.east()).is(pBlock);
        }else flag5 = false;
        if (!AnyEquals(blackListedBlockPos,pPos)) {
            flag6 = pLevel.getBlockState(pPos.west()).is(pBlock);
        }else flag6 = false;
        return flag1||flag2||flag3||flag4||flag5||flag6;
    }

    static boolean AnyEquals(Integer[] massive,int number){
        for (Integer integer : massive) {
            if (integer == number) {
                return true;
            }
        }
        return false;
    }
    static boolean AnyEquals(Direction[] massive,Direction direction){
        for (Direction direction1 : massive) {
            if (direction1 == direction) {
                return true;
            }
        }
        return false;
    }
    static boolean AnyEquals(BlockPos[] massive,BlockPos blockPos){
        for (BlockPos blockPos1 : massive) {
            if (blockPos1 == blockPos) {
                return true;
            }
        }
        return false;
    }
    static boolean AnyEquals(List<BlockPos> massive,BlockPos blockPos){
        for (BlockPos blockPos1 : massive) {
            if (blockPos1 == blockPos) {
                return true;
            }
        }
        return false;
    }
    @Nullable
    static Direction NeighbourDirTowards(Level pLevel, Block[] pBlocks, BlockPos pPos, List<BlockPos> blacklist,BlockPos target){
        int XDiff = Math.abs(target.getX() - pPos.getX());
        int YDiff = Math.abs(target.getY() - pPos.getY());
        int ZDiff = Math.abs(target.getZ() - pPos.getZ());
        //LOGGER.debug("Our target is "+target);
        for (Block pBlock : pBlocks) {
            //LOGGER.debug("Checking "+pBlock+" on "+pPos);
            if (pLevel.getBlockState(pPos.above()).is(pBlock)) {
                //LOGGER.debug(pBlock+" above detected");
                if (!AnyEquals(blacklist, pPos.above())) {
                    //LOGGER.debug(pPos.above()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.above().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.above().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.above().getZ()) < ZDiff) {
                        //LOGGER.debug(Math.abs(target.getX() - pPos.above().getX())+" vs "+XDiff);
                        //LOGGER.debug(Math.abs(target.getY() - pPos.above().getY())+" vs "+YDiff);
                        //LOGGER.debug(Math.abs(target.getZ() - pPos.above().getZ())+" vs "+ZDiff);
                        return Direction.UP;
                    } //else LOGGER.debug("Difference increased");
                }//else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (pLevel.getBlockState(pPos.below()).is(pBlock)) {
                //LOGGER.debug(pBlock+" below detected");
                if (!AnyEquals(blacklist, pPos.below())) {
                    //LOGGER.debug(pPos.below()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.below().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.below().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.below().getZ()) < ZDiff) {
                        //LOGGER.debug(Math.abs(target.getX() - pPos.below().getX())+" vs "+XDiff);
                        //LOGGER.debug(Math.abs(target.getY() - pPos.below().getY())+" vs "+YDiff);
                        //LOGGER.debug(Math.abs(target.getZ() - pPos.below().getZ())+" vs "+ZDiff);
                        return Direction.DOWN;
                    } //else LOGGER.debug("Difference increased");
                }// else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (pLevel.getBlockState(pPos.west()).is(pBlock)) {
                //LOGGER.debug(pBlock+" on west detected");
                if (!AnyEquals(blacklist, pPos.west())) {
                    //LOGGER.debug(pPos.west()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.west().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.west().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.west().getZ()) < ZDiff) {
                       // LOGGER.debug(Math.abs(target.getX() - pPos.west().getX())+" vs "+XDiff);
                        //LOGGER.debug(Math.abs(target.getY() - pPos.west().getY())+" vs "+YDiff);
                       // LOGGER.debug(Math.abs(target.getZ() - pPos.west().getZ())+" vs "+ZDiff);
                        return Direction.WEST;
                    }//else LOGGER.debug("Difference increased");
                }//else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (pLevel.getBlockState(pPos.east()).is(pBlock)) {
                //LOGGER.debug(pBlock+" on east detected");
                if (!AnyEquals(blacklist, pPos.east())) {
                   // LOGGER.debug(pPos.east()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.east().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.east().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.east().getZ()) < ZDiff) {
                        //LOGGER.debug(Math.abs(target.getX() - pPos.east().getX())+" vs "+XDiff);
                       // LOGGER.debug(Math.abs(target.getY() - pPos.east().getY())+" vs "+YDiff);
                        //LOGGER.debug(Math.abs(target.getZ() - pPos.east().getZ())+" vs "+ZDiff);
                        return Direction.EAST;
                    }//else LOGGER.debug("Difference increased");
                }//else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (pLevel.getBlockState(pPos.north()).is(pBlock)) {
                //LOGGER.debug(pBlock+" on north detected");
                if (!AnyEquals(blacklist, pPos.north())) {
                    //LOGGER.debug(pPos.north()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.north().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.north().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.north().getZ()) < ZDiff) {
                        //LOGGER.debug(Math.abs(target.getX() - pPos.north().getX())+" vs "+XDiff);
                       // LOGGER.debug(Math.abs(target.getY() - pPos.north().getY())+" vs "+YDiff);
                       // LOGGER.debug(Math.abs(target.getZ() - pPos.north().getZ())+" vs "+ZDiff);
                        return Direction.NORTH;
                    }//else LOGGER.debug("Difference increased");
                }//else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (pLevel.getBlockState(pPos.south()).is(pBlock)) {
                //LOGGER.debug(pBlock+" on south detected");
                if (!AnyEquals(blacklist, pPos.south())) {
                    //LOGGER.debug(pPos.south()+" is not blacklisted");
                    if (Math.abs(target.getX() - pPos.south().getX()) < XDiff
                            || Math.abs(target.getY() - pPos.south().getY()) < YDiff
                            || Math.abs(target.getZ() - pPos.south().getZ()) < ZDiff) {
                        //LOGGER.debug(Math.abs(target.getX() - pPos.south().getX())+" vs "+XDiff);
                       // LOGGER.debug(Math.abs(target.getY() - pPos.south().getY())+" vs "+YDiff);
                        //LOGGER.debug(Math.abs(target.getZ() - pPos.south().getZ())+" vs "+ZDiff);
                        return Direction.SOUTH;
                    }//else LOGGER.debug("Difference increased");
                }//else LOGGER.debug(pPos.above()+" is blacklisted");
            }
            if (Math.abs(target.getX() - pPos.getX()) == 0
                    && Math.abs(target.getY() - pPos.getY()) == 0
                    && Math.abs(target.getZ() - pPos.getZ()) == 0) {
                //LOGGER.debug("Target reached");
                return null;
            }
        }
        return null;
    }
    static boolean HasWayFromSource(Level pLevel, Block[] pBlocks, BlockPos pPos, BlockPos source){
        /*
        return (pLevel.getBlockState(pPos.above()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.above(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.below()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.below(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.north()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.north(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.south()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.south(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.east()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.east(), new Direction[]{Direction.EAST, Direction.WEST}))
                || (pLevel.getBlockState(pPos.west()).is(pBlocks) || HasNeighbour(pLevel, pBlocks, pPos.west(), new Direction[]{Direction.EAST, Direction.WEST}));
    */


        BlockPos point = source;
        List<BlockPos> Passed = new ArrayList<>();
        boolean noWay = false;
        //LOGGER.debug("Our target is "+pPos);
        do {
            Direction direction = NeighbourDirTowards(pLevel, pBlocks, point, Passed,pPos);
            if (direction != null) {
                //LOGGER.debug("Block exist on "+ direction);
                //LOGGER.debug("Block position "+point.relative(NeighbourDirTowards(pLevel, block, point, Passed,pPos))+" is on the way");
                Passed.add(point);
                point = point.relative(direction);

            }else noWay = true;
            if (point.getX() == pPos.getX()
                    && point.getY() == pPos.getY()
                    && point.getZ() == pPos.getZ()) {
                //LOGGER.debug(point+" is equal to "+pPos);
                return true;
            }
        } while (!noWay);
        //LOGGER.debug("Here is no way");
        return false;
    }

}

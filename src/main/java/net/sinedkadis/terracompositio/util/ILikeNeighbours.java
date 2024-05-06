package net.sinedkadis.terracompositio.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface ILikeNeighbours {
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
    static boolean HasNeighbour(Level pLevel,Block pBlock,BlockPos pPos){
        return (pLevel.getBlockState(pPos.above()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.above(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.below()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.below(), new Direction[]{Direction.UP, Direction.DOWN}))
                || (pLevel.getBlockState(pPos.north()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.north(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.south()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.south(), new Direction[]{Direction.NORTH, Direction.SOUTH}))
                || (pLevel.getBlockState(pPos.east()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.east(), new Direction[]{Direction.EAST, Direction.WEST}))
                || (pLevel.getBlockState(pPos.west()).is(pBlock) || HasNeighbour(pLevel, pBlock, pPos.west(), new Direction[]{Direction.EAST, Direction.WEST}));
    }
}

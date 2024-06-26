package net.sinedkadis.terracompositio.block.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.util.ModGameRules;
import net.sinedkadis.terracompositio.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FlowingFlowCedarLikeBlock extends RotatedPillarBlock {
    private static final Logger LOGGER = LogUtils.getLogger();

    public FlowingFlowCedarLikeBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem){
            if(state.is(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get())){
                return ModBlocks.STRIPPED_FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if(state.is(ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get())){
                return ModBlocks.STRIPPED_FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context,toolAction,simulate);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (pNewState.is(Blocks.AIR)) {
            if (!pLevel.getGameRules().getBoolean(ModGameRules.DISABLE_FLOW_LEAKING)) {
                List<BlockPos> totoReplace = getNearBlocks(pPos);
                for (BlockPos pos : totoReplace) {
                    if (pos != pPos){
                        if (pLevel.getBlockState(pos).is(ModTags.Blocks.FLOWING_FLOW_CEDAR_LOGS)) {
                            List<BlockPos> toReplace = getNearBlocks(pos);
                            for (BlockPos blockPos : toReplace) {
                                if (!AnyEquals(totoReplace,blockPos)) {
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get())) {
                                        pLevel.setBlockAndUpdate(blockPos,
                                                ModBlocks.FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                                    }
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get())) {
                                        pLevel.setBlockAndUpdate(blockPos,
                                                ModBlocks.FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                                    }
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_PORT.get())) {
                                        pLevel.setBlockAndUpdate(blockPos,
                                                ModBlocks.FLOW_PORT.get().defaultBlockState());
                                    }
                                }
                            }
                        }
                    }
                }
                for (BlockPos blockPos : totoReplace) {
                    if (blockPos != pPos) {
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                        }
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                        }
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOWING_FLOW_PORT.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOW_PORT.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    static @NotNull List<BlockPos> getNearBlocks(BlockPos pPos) {
        List<BlockPos> toReplace = new ArrayList<>();
        for (int x = -1;x<=1;x++){
            for (int y = -1;y<=1;y++){
                for (int z = -1;z<=1;z++){
                    toReplace.add(new BlockPos(pPos.getX() + x,
                            pPos.getY() + y,
                            pPos.getZ() + z));
                }
            }
        }
        return toReplace;
    }

    static boolean AnyEquals(List<BlockPos> massive,BlockPos blockPos){
        for (BlockPos blockPos1 : massive) {
            if (blockPos1 == blockPos) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        for (BlockPos blockPos : getNearBlocks(pPos)){
            if (blockPos != pPos){
                //LOGGER.debug("Trying to replace at {}",blockPos);
                if (pLevel.getBlockState(blockPos).is(ModTags.Blocks.FLOW_CEDAR_LOGS)) {
                    float random = pRandom.nextFloat();
                    //LOGGER.debug("Block has tag, random is {}",random);
                    if (random > 0.99f) {
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_CEDAR_LOG.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOWING_FLOW_CEDAR_LOG.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                        }
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_CEDAR_WOOD.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get().defaultBlockState().setValue(AXIS, pLevel.getBlockState(blockPos).getValue(AXIS)));
                        }
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                            pLevel.setBlockAndUpdate(blockPos,
                                    ModBlocks.FLOWING_FLOW_PORT.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
}

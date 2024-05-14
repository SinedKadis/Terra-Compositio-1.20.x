package net.sinedkadis.terracompositio.block.custom;


import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.network.NetworkHooks;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.block.entity.FlowPortBlockEntity;
import net.sinedkadis.terracompositio.block.entity.ModBlockEntities;
import net.sinedkadis.terracompositio.util.ILikeNeighbours;
import net.sinedkadis.terracompositio.util.ModGameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import net.minecraft.world.level.block.RotatedPillarBlock;

import static net.sinedkadis.terracompositio.util.ILikeNeighbours.AnyEquals;


public class FlowWoodPortBlock extends BaseEntityBlock {
    public FlowWoodPortBlock(Properties pProperties) {
        super(pProperties);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem){
            if(state.is(ModBlocks.FLOW_PORT.get())){
                return ModBlocks.STRIPPED_NONFLOW_LOG.get().defaultBlockState();
            }

        }
        return super.getToolModifiedState(state, context,toolAction,simulate);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()){
            BlockEntity blockEntity =pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof FlowPortBlockEntity){
                ((FlowPortBlockEntity) blockEntity).drops();
            }
        }
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
                for (int b = 0;b<5;b++){
                    IndexBlackList[ind++] = b;
                    IndexBlackList[ind++] = b+20;
                    IndexBlackList[ind++] = b+100;
                    IndexBlackList[ind++] = b+120;
                }
                for (int v = 0;v<5;v++){
                    IndexBlackList[ind++] =5*v;
                    IndexBlackList[ind++] =(5*(v+1))-1;
                    IndexBlackList[ind++] =(5*v)+100;
                    IndexBlackList[ind++] =(5*(v+1))-1+100;
                }
                for (int c = 0;c<5;c++){
                    IndexBlackList[ind++] =c*25;
                    IndexBlackList[ind++] =(c*25)+4;
                    IndexBlackList[ind++] =20+(25*c);
                    IndexBlackList[ind++] =20+(25*c)+4;
                }
                IndexBlackList[ind++] = 62;


                for (int k = 0;k<125;k++){
                    if (!AnyEquals(IndexBlackList,k)) {
                        BlockPos blockPos = LeakRange[k];
                        if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_LOG.get())
                                ||pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_WOOD.get())
                                ||pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                            //LOGGER.debug("Index "+k+" ("+ LeakRange[k] +") is not blacklisted");
                            if (blockPos != pPos.above()
                                    && blockPos != pPos.below()
                                    && blockPos != pPos.west()
                                    && blockPos != pPos.east()
                                    && blockPos != pPos.north()
                                    && blockPos != pPos.south()) {


                                //LOGGER.debug("Index "+k+" is not nearby source");
                                Block[] flowBlocks = new Block[]{ModBlocks.FLOW_LOG.get(),
                                        ModBlocks.FLOW_WOOD.get(),
                                        ModBlocks.FLOW_PORT.get(),
                                        ModBlocks.NONFLOW_LOG.get(),
                                        ModBlocks.NONFLOW_WOOD.get(),
                                        ModBlocks.NONFLOW_PORT.get()};
                                if (ILikeNeighbours.HasWayFromSource(pLevel, flowBlocks, blockPos, pPos)) {
                                    //LOGGER.debug("Log like is detected on index "+k+" ("+ blockPos +"), replacing");
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_LOG.get())) {
                                        //LOGGER.debug("It`s log");
                                        pLevel.setBlock(blockPos, ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, pLevel.getBlockState(blockPos).getValue(RotatedPillarBlock.AXIS)), 2);
                                    }
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_WOOD.get())) {
                                        //LOGGER.debug("It`s wood");
                                        pLevel.setBlock(blockPos, ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, pLevel.getBlockState(blockPos).getValue(RotatedPillarBlock.AXIS)), 2);
                                    }
                                    if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                                        //LOGGER.debug("It`s port");
                                        pLevel.setBlock(blockPos, ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
                                    }
                                }
                            } else {
                                //LOGGER.debug("Index "+k+" is nearby source");
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_LOG.get())) {
                                    //LOGGER.debug("It`s log");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, pLevel.getBlockState(blockPos).getValue(RotatedPillarBlock.AXIS)), 2);
                                }
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_WOOD.get())) {
                                    //LOGGER.debug("It`s wood");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, pLevel.getBlockState(blockPos).getValue(RotatedPillarBlock.AXIS)), 2);
                                }
                                if (pLevel.getBlockState(blockPos).is(ModBlocks.FLOW_PORT.get())) {
                                    //LOGGER.debug("It`s port");
                                    pLevel.setBlock(blockPos, ModBlocks.NONFLOW_PORT.get().defaultBlockState(), 2);
                                }
                            }
                        }


                    }//else LOGGER.debug("Index "+k+" ("+ LeakRange[k] +") is blacklisted");
                }


                //pLevel.playSeededSound(null,pPos.getX(),pPos.getY(),pPos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS,0.1f,1.5f,0);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        //if (!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof  FlowPortBlockEntity){
                ItemStack itemstack = pPlayer.getItemInHand(pHand);
                ItemStack outputSlot = ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getOutputSlot();
                ItemStack inputSlot = ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot();
                if (outputSlot.isEmpty() && inputSlot.isEmpty()){
                    //LOGGER.debug("Block is empty");
                    if(!itemstack.isEmpty()) {
                        /*LOGGER.debug("Putting "+*/ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).addItemInSlot(0,itemstack,1)/*)*/;
                     pLevel.playSound(pPlayer,pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
                    }
                }else if (!outputSlot.isEmpty()){
                    //LOGGER.debug("Output is not empty, putting "+outputSlot+" in inventory");
                    if (!pPlayer.addItem(outputSlot)) {
                        pPlayer.drop(outputSlot, false);

                        //LOGGER.debug("Dropped");
                    }
                    ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).setSlotEmpty(1);
                    pLevel.playSound(pPlayer,pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                } else if (!inputSlot.isEmpty()){
                    //LOGGER.debug("Input is not empty, putting "+inputSlot+" in inventory");
                    if (!pPlayer.addItem(inputSlot)) {
                        pPlayer.drop(inputSlot, false);

                        //LOGGER.debug("Dropped");
                    }
                    ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).setSlotEmpty(0);
                    pLevel.playSound(pPlayer,pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                }  else  {
                    NetworkHooks.openScreen(((ServerPlayer) pPlayer), ((FlowPortBlockEntity) entity),pPos);
                }

            }else {
                throw new IllegalStateException("Our Container provider is missing");
            }
        //}

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FlowPortBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.FLOW_PORT_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1,pPos,pState1));
    }
}
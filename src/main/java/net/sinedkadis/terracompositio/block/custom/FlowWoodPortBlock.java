package net.sinedkadis.terracompositio.block.custom;


import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.network.NetworkHooks;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.block.entity.FlowPortBlockEntity;
import net.sinedkadis.terracompositio.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


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
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
       if (!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof  FlowPortBlockEntity){
                ItemStack itemstack = pPlayer.getItemInHand(pHand);
                if (!ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getOutputSlot().isEmpty()){
                    LOGGER.debug("Output is not empty, putting "+ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getOutputSlot()+" in inventory");
                    if (!pPlayer.addItem(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getOutputSlot())) {
                        pPlayer.drop(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getOutputSlot(), false);
                    }
                } else if (!ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot().isEmpty()){
                    LOGGER.debug("Input is not empty, putting "+ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot()+" in inventory");
                    if (!pPlayer.addItem(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot())) {
                        pPlayer.drop(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot(), false);
                    }
                } else if (!itemstack.isEmpty()){
                    LOGGER.debug("Hand is not empty, trying to put "+itemstack+" in input slot");
                    if (!ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot().isEmpty()){
                        LOGGER.debug("Input is not empty, putting "+ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot()+" in inventory");
                        if (!pPlayer.addItem(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot())) {
                            pPlayer.drop(ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).getInputSlot(), false);
                        }
                    }else {
                        ModBlockEntities.FLOW_PORT_BE.get().getBlockEntity(pLevel,pPos).setSlotInput(itemstack);
                        itemstack.shrink(1);
                        LOGGER.debug("Success");
                    }
                } else  {
                    NetworkHooks.openScreen(((ServerPlayer) pPlayer), ((FlowPortBlockEntity) entity),pPos);
                }

            }else {
                throw new IllegalStateException("Our Container provider is missing");
            }
        }

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
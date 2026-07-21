package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sinedkadis.terracompositio.block.entity.FlowCedarTankBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;
import net.sinedkadis.terracompositio.registries.*;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlowCedarTankBlock extends TCBaseEntityBlock{
    public static final IntegerProperty STAGE = IntegerProperty.create("stage",0,4);
    public FlowCedarTankBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STAGE);
    }


    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }


    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        Integer stage = state.getValue(STAGE);
        if (ItemAbilities.AXE_STRIP == itemAbility && (stage.equals(0) || stage.equals(1))) {
            if (stage.equals(0)){
                WorldHelperInternal.flowLeak(state, context.getLevel(), context.getClickedPos());
            }
            return state.setValue(STAGE, 2);
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        return stateForPlacement != null ? stateForPlacement.setValue(STAGE, 3) : null;
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack item2 = player.getItemInHand(InteractionHand.OFF_HAND);

        if (heldItem.is(Items.GLASS) && state.getValue(STAGE).equals(2)
                && (item2.is(TCTags.Items.WRENCHES) || item2.is(TCItems.WRENCH_AXE.get()))) {
            if (!item2.is(TCItems.WRENCH_AXE.get()) || WrenchAxeItem.getWrenchMode(item2).equals(WrenchAxeItem.WrenchMode.WRENCH)) {
                WorldHelperInternal.handleInWorldBlockCraft(state, state.setValue(STAGE, 3), level, pos, heldItem, 1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        IFluidHandler fluidHandlerBlock = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (!(fluidHandlerBlock instanceof FluidTank tank)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        IFluidHandlerItem fluidHandlerItem = heldItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null) {
            FluidStack fluidStack = new FluidStack(TCFluids.FLOW_FLUID.source.get().getSource(), 250);
            if (heldItem.is(TCItems.FLOW_BOTTLE.get())){
                int filled = tank.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if (filled == 250){
                    tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    heldItem.shrink(1);
                    player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                    level.playSound(player, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            if (heldItem.getItem() instanceof BottleItem) {
                FluidStack drained = tank.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if (FluidStack.matches(drained, fluidStack)) {
                    tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    heldItem.shrink(1);
                    player.getInventory().add(new ItemStack(TCItems.FLOW_BOTTLE.get()));
                    level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
                    return ItemInteractionResult.SUCCESS;
                }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }


        FluidStack transferred = FluidUtil.tryFluidTransfer(tank, fluidHandlerItem, Integer.MAX_VALUE, false);

        if (!transferred.isEmpty()) {
            if (!player.getAbilities().instabuild) {
                FluidUtil.tryFluidTransfer(tank, fluidHandlerItem, Integer.MAX_VALUE, true);
                player.setItemInHand(hand, fluidHandlerItem.getContainer());
            } else {
                tank.fill(transferred, IFluidHandler.FluidAction.EXECUTE);
            }
            level.playSound(
                    null,
                    pos,
                    SoundEvents.BUCKET_EMPTY,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            return ItemInteractionResult.SUCCESS;
        }


        transferred = FluidUtil.tryFluidTransfer(fluidHandlerItem, tank, Integer.MAX_VALUE, false);

        if (!transferred.isEmpty()) {
            if (!player.getAbilities().instabuild) {
                FluidUtil.tryFluidTransfer(fluidHandlerItem, tank, Integer.MAX_VALUE, true);
                player.setItemInHand(hand, fluidHandlerItem.getContainer());
            } else {
                tank.drain(transferred, IFluidHandler.FluidAction.EXECUTE);
            }
            level.playSound(
                    null,
                    pos,
                    SoundEvents.BUCKET_FILL,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            return ItemInteractionResult.SUCCESS;
        }


        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FlowCedarTankBlockEntity flowCedarTankBlockEntity) {
            flowCedarTankBlockEntity.scheduleMemberUpdate();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = new ArrayList<>();
        ItemStack inHand = pParams.getParameter(LootContextParams.TOOL);
        if (inHand.getItem() instanceof AxeItem){
            return super.getDrops(pState,pParams);
        }
        switch (pState.getValue(STAGE)){
            case 0,1,2 -> {
                drops.add(new ItemStack(TCBlocks.FLOW_CEDAR_LOG.get()));
                drops.add(new ItemStack(TCItems.GOLD_ROD.get(),4));
                drops.add(new ItemStack(TCItems.INFUSED_IRON_ROD.get(),8));
            }
            case 3,4 -> {
                drops.add(new ItemStack(TCBlocks.FLOW_CEDAR_LOG.get()));
                drops.add(new ItemStack(TCItems.GOLD_ROD.get(),4));
                drops.add(new ItemStack(TCItems.INFUSED_IRON_ROD.get(),8));
                drops.add(Items.GLASS.getDefaultInstance());
            }
        }
        return drops;
    }

    @Override
    public BlockEntityType<? extends TCBlockEntity> getBlockEntityType() {
        return TCBlockEntities.FLOW_CEDAR_TANK_BE.get();
    }
}

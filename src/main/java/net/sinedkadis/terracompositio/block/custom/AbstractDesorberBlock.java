package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractDesorberBlock extends TCBaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final BooleanProperty INFUSED;
    protected static final BooleanProperty WATERLOGGED;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(INFUSED,WATERLOGGED);
    }

    public @NotNull FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    protected AbstractDesorberBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED,false));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        IFluidHandler fluidHandlerBlock = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.DOWN);
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

                    level.playSound(null,pos,SoundEvents.BOTTLE_EMPTY,SoundSource.BLOCKS);

                    return ItemInteractionResult.SUCCESS;
                }
            }
            if (heldItem.getItem() instanceof BottleItem) {
                FluidStack drained = tank.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if (FluidStack.matches(drained, fluidStack)) {
                    tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    heldItem.shrink(1);
                    player.getInventory().add(new ItemStack(TCItems.FLOW_BOTTLE.get()));

                    level.playSound(null,pos,SoundEvents.BUCKET_FILL,SoundSource.BLOCKS);

                    return ItemInteractionResult.SUCCESS;
                }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {

            FluidStack transferred = FluidUtil.tryFluidTransfer(tank, fluidHandlerItem, Integer.MAX_VALUE, true);

            if (!transferred.isEmpty()) {

                player.setItemInHand(hand, fluidHandlerItem.getContainer());
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


            transferred = FluidUtil.tryFluidTransfer(fluidHandlerItem, tank, Integer.MAX_VALUE, true);

            if (!transferred.isEmpty()) {
                player.setItemInHand(hand, fluidHandlerItem.getContainer());
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
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }



    static {
        INFUSED = TCBlockStateProperties.INFUSED;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}

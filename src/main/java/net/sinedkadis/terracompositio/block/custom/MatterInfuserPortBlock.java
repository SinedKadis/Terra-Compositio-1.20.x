package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.sinedkadis.terracompositio.api.helpers.PlayerHelper;
import net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour;
import net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour.hasSpace;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MatterInfuserPortBlock extends MatterInfuserBaseEntityBlock {

    public MatterInfuserPortBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntityType<? extends TCBlockEntity> getBlockEntityType() {
        return TCBlockEntities.MATTER_INFUSER_PORT_BE.get();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        BlockPos casingPos = pos.relative(state.getValue(FACING).getOpposite());
        FlowCedarCasingBlockEntity casingBE = (FlowCedarCasingBlockEntity) level.getBlockEntity(casingPos);
        if (casingBE != null) {
            ItemHandlerBehaviour itemBehaviour = casingBE.getItemBehaviours().stream()
                    .filter(ItemHandlerBehaviour.class::isInstance)
                    .map(ItemHandlerBehaviour.class::cast)
                    .findAny().orElse(null);
            if (itemBehaviour != null) {
                IItemHandlerModifiable itemHandler = itemBehaviour.getItemHandler();
                int i = FlowCedarCasingBlockEntity.INPUT_INVENTORY_SLOT;
                ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                if (!stackInSlot.isEmpty()) {

                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);

                    PlayerHelper.addOrDropToPlayer(player, stackInSlot, true);
                    level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                    return ItemInteractionResult.SUCCESS;
                }
                if (!itemInHand.isEmpty() && itemBehaviour.allowInsert(i, itemInHand, Direction.UP, true)
                        && hasSpace(itemHandler, i)
                        && (ItemStack.isSameItem(itemInHand, stackInSlot) || stackInSlot.isEmpty())) {


                    int count = itemInHand.getCount() + stackInSlot.getCount();
                    int left = count - 64;
                    ItemStack handCopy = itemInHand.copy();
                    ItemStack storageCopy = stackInSlot.copy();
                    if (left > 0) {
                        storageCopy.setCount(64);
                        handCopy.setCount(left);
                    } else {
                        if (stackInSlot.isEmpty()) storageCopy = handCopy;
                        storageCopy.setCount(count);
                        handCopy = ItemStack.EMPTY;
                    }
                    itemHandler.setStackInSlot(i, storageCopy);
                    player.setItemInHand(hand, handCopy);
                    level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}

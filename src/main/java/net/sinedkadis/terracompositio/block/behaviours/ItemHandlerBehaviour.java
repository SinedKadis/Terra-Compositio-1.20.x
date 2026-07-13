package net.sinedkadis.terracompositio.block.behaviours;

import lombok.Data;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.PlayerHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.tooltip_components.ItemComponent;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEItemBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Data
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemHandlerBehaviour implements IBEItemBehaviour, WorldlyContainer, IHaveKnowledge {
    private final TCBlockEntity blockEntity;

    protected ItemStackHandler itemHandler = new ItemStackHandler() {
        @Override
        public int getSlotLimit(int slot) {
            return getLimitInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!allowInsert(slot, stack, null, true)) return stack;
            ItemStack itemStack = super.insertItem(slot, stack, simulate);
            Level level = blockEntity.getLevel();
            if (level != null && !simulate) {
                level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
            return itemStack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // 511 - hardcoded number to bypass restriction when extracting with crowbar
            if (!allowExtract(slot, getStackInSlot(slot), null, true) && !(amount == 511))
                return ItemStack.EMPTY;
            ItemStack itemStack = super.extractItem(slot, amount, simulate);
            Level level = blockEntity.getLevel();
            if (level != null && !simulate) {
                level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
            return itemStack;
        }
    };

    //One slot
    public ItemHandlerBehaviour(TCBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public ItemHandlerBehaviour(TCBlockEntity blockEntity, int slotCount) {
        this.blockEntity = blockEntity;
        itemHandler.setSize(slotCount);
    }

    public int getLimitInSlot(int slot) {
        return 64;
    }

    @Override
    public boolean allowExtract(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
        return true;
    }

    @Override
    public boolean allowInsert(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
        return true;
    }

    @Override
    public void tick() {

    }

    @Override
    public void onChunkLoad() {

    }


    @Override
    public void onRemoved() {

    }


    @Override
    public void onSave(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag pValue = getItemHandler().serializeNBT(registries);
        tag.put("itemHandler", pValue);
    }

    public static boolean hasSpace(IItemHandlerModifiable itemHandler, int i) {
        return itemHandler.getSlotLimit(i) - itemHandler.getStackInSlot(i).getCount() > 0;
    }

    @Override
    public void onLoad(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag itemHandler1 = tag.getCompound("itemHandler");
        getItemHandler().deserializeNBT(registries, itemHandler1);
    }

    @Override
    public InteractionResult onUse(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        Level level = blockEntity.getLevel();
        if (level == null) return InteractionResult.PASS;

        BlockPos blockPos = blockEntity.getBlockPos();

        ItemStack itemInHand = pPlayer.getItemInHand(pHand);

        ItemStackHandler itemHandler = getItemHandler();
        boolean[] isEmptySlot = new boolean[itemHandler.getSlots()];
        boolean hasEmptySlots = false;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).isEmpty()
                    && allowInsert(i, itemInHand, pHit.getDirection(), true)
                    && hasSpace(itemHandler, i)) {
                hasEmptySlots = true;
                isEmptySlot[i] = true;
            }
        }

        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            ItemStack slot = itemHandler.getStackInSlot(i);

            boolean hasItemInHand = !itemInHand.isEmpty();
            if (hasItemInHand && hasEmptySlots) {
                if (isEmptySlot[i]) {
                    ItemStack left = itemHandler.insertItem(i, itemInHand.copy(), false);
                    pPlayer.setItemInHand(pHand, left);
                    level.playSound(pPlayer, blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
                    blockEntity.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
            if ((!hasEmptySlots || !hasItemInHand || i == itemHandler.getSlots() - 1) && !slot.isEmpty() && allowExtract(i, slot, pHit.getDirection(), true)) {
                ItemStack extracted = itemHandler.extractItem(i, 64, false);
                PlayerHelper.addOrDropToPlayer(pPlayer, extracted);
                level.playSound(pPlayer, blockPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                blockEntity.setChanged();
                return InteractionResult.SUCCESS;
            }

        }


        return InteractionResult.PASS;
    }


    @Override
    public int[] getSlotsForFace(Direction direction) {
        int slots = itemHandler.getSlots();
        int[] ints = new int[slots];
        for (int i = 0; i < slots; i++) {
            ints[i] = i;
        }
        return ints;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return allowInsert(pIndex, pItemStack, pDirection, false);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return allowExtract(pIndex, pStack, pDirection, false);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack slot = itemHandler.getStackInSlot(i);
            if (slot.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getItemHandler().getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return getItemHandler().extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stackInSlot = getItemHandler().getStackInSlot(pSlot);
        getItemHandler().setStackInSlot(pSlot, ItemStack.EMPTY);
        return stackInSlot;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        getItemHandler().setStackInSlot(pSlot, pStack);
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(blockEntity, pPlayer);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            list.add(itemHandler.getStackInSlot(i));
        }
        data.put("inventory", ItemHelper.writeItemList(list, provider));
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.ITEMS, tooltip, t -> {
            List<ItemStack> entries = ItemHelper.readItemList(data.getList("inventory", Tag.TAG_COMPOUND), provider);
            for (ItemStack stack : entries) {
                if (stack.isEmpty()) continue;
                t.add(ItemComponent.of(stack));

            }
        });
    }

    @Override
    public IItemHandler getItemCapability(@Nullable Direction direction) {
        return itemHandler;
    }
}

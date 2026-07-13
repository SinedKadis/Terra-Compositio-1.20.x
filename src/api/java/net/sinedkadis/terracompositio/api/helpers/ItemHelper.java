package net.sinedkadis.terracompositio.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The Class with cool methods, that my mod use, related to {@link ItemStack}.
 */
public class ItemHelper {
    /**
     * Gets all items, that contains given stack. Includes schulker boxes, bundles and other.
     *
     * @param containerStack the container stack
     * @return the container contents
     */
    public static Stream<ItemStack> getContainerContents(ItemStack containerStack) {
        if (containerStack.isEmpty()) {
            return Stream.of();
        }
        ItemContainerContents itemContainerContents = containerStack.get(DataComponents.CONTAINER);
        if (itemContainerContents == null) return Stream.of();
        return ((NonNullList<ItemStack>) itemContainerContents.nonEmptyItemsCopy()).stream();

    }

    /**
     * Reads items from {@link ListTag}.
     *
     * @param itemsTag the items tag
     * @param provider the registry access
     * @return the list
     */
    public static List<ItemStack> readItemList(ListTag itemsTag, HolderLookup.Provider provider) {
        List<ItemStack> items = new ArrayList<>(itemsTag.size());

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag itemTag = itemsTag.getCompound(i);
            ItemStack itemStack = ItemStack.parseOptional(provider, itemTag);
            items.add(itemStack);
        }

        return items;
    }

    /**
     * Writes items to {@link ListTag}.
     *
     * @param stacks   the items
     * @param provider the registry access
     * @return the list tag
     */
    public static ListTag writeItemList(Iterable<ItemStack> stacks, HolderLookup.Provider provider) {
        return writeCompoundList(stacks, itemStack -> (CompoundTag) itemStack.saveOptional(provider));
    }

    /**
     * Writes things from list to {@link ListTag}, using serializer to convert entries. I think I take that somewhere, but I don't remember where
     *
     * @param <T>        the type parameter
     * @param list       the list of things
     * @param serializer the serializer function
     * @return the list tag
     */
    public static <T> ListTag writeCompoundList(Iterable<T> list, Function<T, CompoundTag> serializer) {
        ListTag listNBT = new ListTag();
        list.forEach(t -> {
            CompoundTag apply = serializer.apply(t);
            if (apply == null)
                return;
            listNBT.add(apply);
        });
        return listNBT;
    }

    /**
     * Drop contents of blockEntity default inventory.
     *
     * @param blockEntity the block entity
     * @param slots       the last slot index, that will be dropped. If Empty, all slots will be dropped
     */
    public static void dropContents(BlockEntity blockEntity, int... slots) {
        dropContents(blockEntity, Capabilities.ItemHandler.BLOCK, slots);
    }

    /**
     * Drop contents of blockEntity given cap.
     *
     * @param <T>         the type parameter
     * @param blockEntity the block entity
     * @param cap         the capability that extends {@link IItemHandler}
     * @param slots       the last slot index, that will be dropped. If Empty, all slots will be dropped
     */
    public static <T extends IItemHandler, C> void dropContents(BlockEntity blockEntity, BlockCapability<T, C> cap, int... slots) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        T itemHandler = level.getCapability(cap, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        if (itemHandler == null) return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        if (slots.length == 0) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inventory.setItem(i, itemHandler.getStackInSlot(i));
            }
        } else {
            for (int i = 0; i < slots.length; i++) {
                int slot = slots[i];
                inventory.setItem(i, itemHandler.getStackInSlot(slot));
            }
        }
        BlockPos worldPosition = blockEntity.getBlockPos();
        Containers.dropContents(level, worldPosition, inventory);

    }

    public static void hurtAndBreakItem(ServerLevel level, Player player, ItemStack item) {
        item.hurtAndBreak(1, level, player, player1 -> EventHooks.onPlayerDestroyItem(player, item, InteractionHand.MAIN_HAND));
    }
}

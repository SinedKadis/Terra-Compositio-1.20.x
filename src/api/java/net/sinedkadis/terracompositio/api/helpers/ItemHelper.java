package net.sinedkadis.terracompositio.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

        List<ItemStack> result = new ArrayList<>();
        Deque<ItemStack> queue = new ArrayDeque<>();

        enqueueChildren(containerStack, queue);

        while (!queue.isEmpty()) {
            ItemStack stack = queue.poll();
            if (stack.isEmpty()) continue;

            result.add(stack);
            enqueueChildren(stack, queue);
        }

        return result.stream();
    }

    private static void enqueueChildren(ItemStack stack, Deque<ItemStack> queue) {
        ItemContainerContents containerContents = stack.get(DataComponents.CONTAINER);
        if (containerContents != null) {
            containerContents.nonEmptyItemsCopy().forEach(queue::add);
        }
        if (stack.is(Items.BUNDLE)) {
            BundleContents bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (bundleContents != null) {
                bundleContents.itemsCopy().forEach(queue::add);
            }
        }
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
     * Drop contents of blockEntity given cap.
     *
     * @param <T>         the type parameter
     * @param <C>         the type parameter
     * @param blockEntity the block entity
     * @param cap         the capability that extends {@link IItemHandler}
     */
    public static <T extends IItemHandler, C> void dropContents(BlockEntity blockEntity, BlockCapability<T, C> cap) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        dropContents(level, blockEntity.getBlockPos(), cap);
    }

    /**
     * Drop contents of given item capability.
     *
     * @param <T>      the item handler type parameter
     * @param level    the level
     * @param blockPos the block pos
     * @param cap      the item handler cap
     */
    public static <T extends IItemHandler, C> void dropContents(Level level, BlockPos blockPos, BlockCapability<T, C> cap) {
        T itemHandler = level.getCapability(cap, blockPos, null);
        if (itemHandler == null) return;
        dropContents(level, blockPos, itemHandler);
    }

    /**
     * Drop contents of given item handler.
     *
     * @param <T>         the type parameter
     * @param level       the level
     * @param blockPos    the block pos
     * @param itemHandler the item handler
     */
    public static <T extends IItemHandler> void dropContents(Level level, BlockPos blockPos, T itemHandler) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, blockPos, inventory);
    }

    /**
     * Hurt and break item.
     *
     * @param level  the level
     * @param player the player
     * @param item   the item
     */
    public static void hurtAndBreakItem(ServerLevel level, Player player, ItemStack item) {
        item.hurtAndBreak(1, level, player, player1 -> EventHooks.onPlayerDestroyItem(player, item, InteractionHand.MAIN_HAND));
    }
}

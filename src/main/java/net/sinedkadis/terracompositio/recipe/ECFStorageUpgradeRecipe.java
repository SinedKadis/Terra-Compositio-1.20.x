package net.sinedkadis.terracompositio.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.IECFStorageExtensionItem;
import net.sinedkadis.terracompositio.api.IHaveExtensibleECFStorageItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ECFStorageUpgradeRecipe extends CustomRecipe {

    public static SimpleCraftingRecipeSerializer<ECFStorageUpgradeRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ECFStorageUpgradeRecipe::new);

    public ECFStorageUpgradeRecipe(CraftingBookCategory category) {
        super(category);
    }



    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundWill = false;
        boolean foundItem = false;

        IECFStorageExtensionItem currentExtension = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IHaveExtensibleECFStorageItem cont) {
                    currentExtension = cont.getCurrentExtension(stack);
                    if (foundWill) {
                        return false;
                    }
                    foundWill = true;
                }
            }
        }
        if (currentExtension == null) return false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof IECFStorageExtensionItem extension
                    && extension.maxStorage() > 0) {
                if (currentExtension.self().equals(extension.self())) {
                    return false;
                }
                if (foundItem) {
                    return false;
                }
                foundItem = true;
            }
        }

        return foundItem;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack item = ItemStack.EMPTY;
        IECFStorageExtensionItem extension = () -> 0;
        int emptySlot = -1;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IHaveExtensibleECFStorageItem && item.isEmpty()) {
                    item = stack;
                } else {
                    extension = ((IECFStorageExtensionItem) stack.getItem()); // we already verified this is a storage extension in matches()
                }
            } else {
                if (emptySlot != -1) {
                    continue;
                }
                emptySlot = i;
            }
        }

        IHaveExtensibleECFStorageItem container = (IHaveExtensibleECFStorageItem) item.getItem();

        IECFStorageExtensionItem currentExtension = container.getCurrentExtension(item);
        if (currentExtension.equals(extension) || extension.maxStorage() <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = item.copy();
        container.setExtension(copy, extension);

        return copy;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}

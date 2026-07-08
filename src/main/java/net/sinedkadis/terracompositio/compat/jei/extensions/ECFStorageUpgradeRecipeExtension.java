package net.sinedkadis.terracompositio.compat.jei.extensions;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.sinedkadis.terracompositio.api.IECFStorageExtensionItem;
import net.sinedkadis.terracompositio.api.IHaveExtensibleECFStorageItem;
import net.sinedkadis.terracompositio.recipe.ECFStorageUpgradeRecipe;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ECFStorageUpgradeRecipeExtension implements ICraftingCategoryExtension<ECFStorageUpgradeRecipe> {


	@Override
	public void setRecipe(@NotNull RecipeHolder<ECFStorageUpgradeRecipe> recipeHolder, @NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper helper, @NotNull IFocusGroup focusGroup) {
		List<ItemStack> items = new ArrayList<>();
		List<IECFStorageExtensionItem> extensions = new ArrayList<>(focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getIngredient())
                .filter(itemStack -> itemStack.getItem() instanceof IECFStorageExtensionItem)
                .peek(items::add)
				.map(ItemStack::getItem)
                .map(IECFStorageExtensionItem.class::cast)
                .toList());

		if (extensions.isEmpty()) {
			extensions.addAll(TCItems.ITEMS.getRegistry().get().holders()
					.map(Holder::value)
					.filter(IECFStorageExtensionItem.class::isInstance)
					.map(ItemStack::new)
					.peek(items::add)
					.map(ItemStack::getItem)
					.map(IECFStorageExtensionItem.class::cast)
					.toList()
			);
		}

		List<ItemStack> outputStacks = new ArrayList<>();
		for (var extension : extensions) {
			var stack = new ItemStack(TCItems.TECHNETIUM_LEGGINGS.get());
			((IHaveExtensibleECFStorageItem) stack.getItem()).setExtension(stack, extension);
			outputStacks.add(stack);
		}

		helper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK,
				List.of(Collections.singletonList(new ItemStack(TCItems.TECHNETIUM_LEGGINGS.get())), items), 0, 0);
		helper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, outputStacks);
	}
}

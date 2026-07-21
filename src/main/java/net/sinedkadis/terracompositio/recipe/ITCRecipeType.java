package net.sinedkadis.terracompositio.recipe;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;

public interface ITCRecipeType<INPUT extends RecipeInput, RECIPE extends ITCRecipe<INPUT>> extends RecipeType<RECIPE> {
    INPUT getRecipeInput(TCBlockEntity be);
}

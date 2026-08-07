package net.sinedkadis.terracompositio.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;

public interface ITCRecipeType<INPUT extends Container, RECIPE extends ITCRecipe<INPUT>> extends RecipeType<RECIPE> {
    INPUT getRecipeInput(TCBlockEntity be);
}

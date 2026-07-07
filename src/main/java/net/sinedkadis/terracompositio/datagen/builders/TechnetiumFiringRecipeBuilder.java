package net.sinedkadis.terracompositio.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sinedkadis.terracompositio.recipe.TechnetiumFiringRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TechnetiumFiringRecipeBuilder implements RecipeBuilder {

    private final ItemStack ingredient;
    private final int ecf;
    private TechnetiumFiringRecipeBuilder(ItemStack ingredient,
                                          int ecf) {
        this.ingredient = ingredient;
        this.ecf = ecf;

    }

    public static TechnetiumFiringRecipeBuilder create(Item result,
                                                       int cfe) {
        return new TechnetiumFiringRecipeBuilder(result.getDefaultInstance(), cfe);
    }


    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return ingredient.getItem();
    }


    @Override
    public void save(@NotNull RecipeOutput consumer, @NotNull ResourceLocation pRecipeId) {
        consumer.accept(pRecipeId,new TechnetiumFiringRecipe(ingredient,ecf),null);
    }
}

package net.sinedkadis.terracompositio.datagen.builders;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.recipe.AltarTransformationRecipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AltarTransformationRecipeBuilder implements RecipeBuilder {

    private final ItemStack output;
    private final NonNullList<Ingredient> input;

    private AltarTransformationRecipeBuilder(NonNullList<Ingredient> input, ItemStack output) {
        this.output = output;
        this.input = input;
    }

    public static AltarTransformationRecipeBuilder create(ItemStack output, Ingredient... input) {
        return new AltarTransformationRecipeBuilder(NonNullList.of(Ingredient.EMPTY, input), output);
    }


    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation pRecipeId) {
        consumer.accept(pRecipeId,new AltarTransformationRecipe(input,output),null);
    }


    @Override
    public void save(RecipeOutput consumer, String resourceLocation) {
        this.save(consumer, TerraCompositio.modLoc(AltarTransformationRecipe.Type.ID + "/" + resourceLocation));
    }
}

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
import net.sinedkadis.terracompositio.recipe.FlowInfusionRecipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FlowInfusionRecipeBuilder implements RecipeBuilder {

    private final ItemStack output;
    private final NonNullList<Ingredient> input;
    private final int ecf;
    private final int time;

    private FlowInfusionRecipeBuilder(ItemStack output, NonNullList<Ingredient> input, int ecf, int time) {
        this.output = output;
        this.input = input;
        this.ecf = ecf;
        this.time = time;
    }

    public static FlowInfusionRecipeBuilder create(ItemStack output, NonNullList<Ingredient> input, int cfe, int time) {
        return new FlowInfusionRecipeBuilder(output, input, cfe, time);
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
    public void save(RecipeOutput consumer, ResourceLocation resourceLocation) {
        consumer.accept(resourceLocation, new FlowInfusionRecipe(input,output,ecf,time),null);
    }
}

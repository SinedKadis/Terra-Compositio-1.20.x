package net.sinedkadis.terracompositio.datagen.builders;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sinedkadis.terracompositio.recipe.MatterInfusionRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatterInfusionRecipeBuilder implements RecipeBuilder {

    private final ItemStack output;
    private final ItemStack input;
    private final Item catalyst;
    private final int ecf;
    private final int time;
    private final int rate;

    private MatterInfusionRecipeBuilder(ItemStack input, ItemStack output, Item catalyst, int ecf, int time, int rate) {
        this.output = output;
        this.input = input;
        this.catalyst = catalyst;
        this.ecf = ecf;
        this.time = time;
        this.rate = rate;
    }

    public static MatterInfusionRecipeBuilder create(ItemStack input, ItemStack output,  Item catalyst, int cfe, int time, int rate) {
        return new MatterInfusionRecipeBuilder(input,output,  catalyst, cfe, time, rate);
    }
    public static MatterInfusionRecipeBuilder create(Item input,int i_count, Item output,int o_count, Item catalyst, int cfe, int time, int rate) {
        return new MatterInfusionRecipeBuilder(new ItemStack(input,i_count),new ItemStack(output,o_count),  catalyst, cfe, time, rate);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput consumer, @NotNull ResourceLocation resourceLocation) {
        consumer.accept(resourceLocation,new MatterInfusionRecipe(output, catalyst.getDefaultInstance(),input,rate,ecf,time),null);
    }
}

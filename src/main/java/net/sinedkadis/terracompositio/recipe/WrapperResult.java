package net.sinedkadis.terracompositio.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;


//Thanks to Botania mod for that cool class
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrapperResult implements RecipeOutput {
    private final RecipeOutput delegate;
    private final Function<Recipe<?>,Recipe<?>> wrapper;

    private WrapperResult(RecipeOutput delegate, Function<Recipe<?>,Recipe<?>> wrapper) {
        this.delegate = delegate;
        this.wrapper = wrapper;
    }

    public static WrapperResult of(RecipeOutput delegate, Function<Recipe<?>,Recipe<?>> wrapper) {
        return new WrapperResult(delegate, wrapper);
    }

    @Override
    public Advancement.Builder advancement() {
        return Advancement.Builder.recipeAdvancement();
    }

    @Override
    public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        delegate.accept(id,wrapper.apply(recipe),advancement,conditions);
    }
}

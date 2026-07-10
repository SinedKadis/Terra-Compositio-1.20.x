package net.sinedkadis.terracompositio.compat.create.compat.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.sinedkadis.terracompositio.compat.create.TCCreateCompat;
import net.sinedkadis.terracompositio.compat.create.compat.jei.categories.ItemApplicationWithWrenchCategory;
import net.sinedkadis.terracompositio.compat.create.compat.jei.categories.ManualApplicationFakeRecipes;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

@JeiPlugin
@ParametersAreNonnullByDefault
public class JEICreateCompatTerraCompositioPlugin implements IModPlugin {

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    //private IIngredientManager ingredientManager;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Objects.requireNonNull(ResourceLocation.tryBuild(TCCreateCompat.MOD_ID, "jei_plugin"));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!ModList.get().isLoaded("create")) return;
        loadCategories();

        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));

    }

    @SuppressWarnings("unused")
    public void loadCategories() {
        allCategories.clear();

        var itemWithWrenchCategory = new ItemWithWranchCategoryBuilder()
                .addRecipes(ManualApplicationFakeRecipes::createRecipes)
                .itemIcon(TCItems.WRENCH_TAG_HOLDER.get())
                .emptyBackground(177, 60)
                .build("item_with_wrench_application", ItemApplicationWithWrenchCategory::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
       // ingredientManager = registration.getIngredientManager();
        if (!ModList.get().isLoaded("create")) return;

        allCategories.forEach(c -> c.registerRecipes(registration));

        //moved to mixin
        //addToItemApplication(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (!ModList.get().isLoaded("create")) return;

        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    private class ItemWithWranchCategoryBuilder {

        private final Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<RecipeHolder<ItemApplicationRecipe>>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public ItemWithWranchCategoryBuilder() {

        }

        public ItemWithWranchCategoryBuilder addRecipeListConsumer(Consumer<List<RecipeHolder<ItemApplicationRecipe>>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public ItemWithWranchCategoryBuilder addRecipes(Supplier<Collection<? extends RecipeHolder<ItemApplicationRecipe>>> collection) {
            return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
        }

        public ItemWithWranchCategoryBuilder icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public ItemWithWranchCategoryBuilder itemIcon(ItemLike item) {
            return icon(new ItemIcon(() -> new ItemStack(item)));
        }

        public ItemWithWranchCategoryBuilder background(IDrawable background) {
            this.background = background;
            return this;
        }

        public ItemWithWranchCategoryBuilder emptyBackground(int width, int height) {
            return background(new EmptyBackground(width, height));
        }

        public CreateRecipeCategory<ItemApplicationRecipe> build(String name, CreateRecipeCategory.Factory<ItemApplicationRecipe> factory) {
            Supplier<List<RecipeHolder<ItemApplicationRecipe>>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<RecipeHolder<ItemApplicationRecipe>> recipes = new ArrayList<>();
                    for (Consumer<List<RecipeHolder<ItemApplicationRecipe>>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = Collections::emptyList;
            }

            CreateRecipeCategory.Info<ItemApplicationRecipe> info = new CreateRecipeCategory.Info<>(
                    createRecipeHolderType(Create.asResource(name)),
                    CreateLang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<ItemApplicationRecipe> category = factory.create(info);
            allCategories.add(category);
            return category;
        }
    }


}

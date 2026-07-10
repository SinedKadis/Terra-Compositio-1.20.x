package net.sinedkadis.terracompositio.compat.create.compat.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.compat.create.TCCreateCompat;
import net.sinedkadis.terracompositio.compat.create.compat.jei.categories.ItemApplicationWithWrenchCategory;
import net.sinedkadis.terracompositio.compat.create.compat.jei.categories.ManualApplicationFakeRecipes;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

        var itemWithWrenchCategory = new CategoryBuilder<ItemApplicationRecipe,RecipeHolder<ItemApplicationRecipe>>()
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

        registration.addRecipes(new mezz.jei.api.recipe.RecipeType<>(Create.asResource("item_application"), ItemApplicationRecipe.class),
                List.of(new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new, TerraCompositio.modLoc("cedar_tank_2"))
                                .require(TCBlocks.FLOW_CEDAR_TANK_3.get())
                                .require(ItemTags.AXES)
                                .output(TCBlocks.FLOW_CEDAR_TANK_2.get())
                                .build(),
                        new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new,
                                TerraCompositio.modLoc("cedar_pedestal"))
                                .require(TCBlocks.FLOW_CEDAR_SAPLING.get())
                                .require(Items.BONE_MEAL)
                                .output(TCBlocks.FLOW_CEDAR_PEDESTAL.get())
                                .build())
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (!ModList.get().isLoaded("create")) return;

        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    @SuppressWarnings("unchecked")
    private class CategoryBuilder<RECIPE extends Recipe<?>, HOLDER extends RecipeHolder<RECIPE>> {
        private final Class<? extends RecipeHolder<RECIPE>> recipeClass;
        private final Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<HOLDER>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder() {
            this.recipeClass = (Class<? extends RecipeHolder<RECIPE>>) RecipeHolder.class;
        }

        public CategoryBuilder<RECIPE, HOLDER> addRecipeListConsumer(Consumer<List<HOLDER>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<RECIPE, HOLDER> addRecipes(Supplier<Collection<? extends HOLDER>> collection) {
            return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
        }

        public CategoryBuilder<RECIPE, HOLDER> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<RECIPE, HOLDER> itemIcon(ItemLike item) {
            return icon(new ItemIcon(() -> new ItemStack(item)));
        }

        public CategoryBuilder<RECIPE, HOLDER> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<RECIPE, HOLDER> emptyBackground(int width, int height) {
            return background(new EmptyBackground(width, height));
        }

        public  CreateRecipeCategory<RECIPE> build(String name, CreateRecipeCategory.Factory<RECIPE> factory) {
            Supplier<List<RecipeHolder<RECIPE>>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<RecipeHolder<RECIPE>> recipes = new ArrayList<>();
                    for (Consumer<List<HOLDER>> consumer : recipeListConsumers)
                        consumer.accept((List<HOLDER>) recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = Collections::emptyList;
            }

            CreateRecipeCategory.Info<RECIPE> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(Create.asResource(name), recipeClass),
                    CreateLang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<RECIPE> category = factory.create(info);
            allCategories.add(category);
            return category;
        }
    }

}

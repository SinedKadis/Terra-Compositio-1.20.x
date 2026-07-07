package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.sinedkadis.terracompositio.components.ingredients.ExcludeItemIngredient;

import javax.annotation.ParametersAreNonnullByDefault;

//Thanks to Botania mod for that cool class
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TagTransferShapedRecipe extends ShapedRecipe {
    public static final RecipeSerializer<TagTransferShapedRecipe> SERIALIZER = new Serializer();
    @Getter
    private final ShapedRecipe shapedRecipe;

    public TagTransferShapedRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getGroup(), shapedRecipe.category(), shapedRecipe.pattern,
                // XXX: Hacky, but compose should always be a vanilla shaped recipe which doesn't do anything with the
                // RegistryAccess
                shapedRecipe.getResultItem(RegistryAccess.EMPTY), shapedRecipe.showNotification());
        this.shapedRecipe = shapedRecipe;
    }

    public TagTransferShapedRecipe(Recipe<?> compose) {
        this(((ShapedRecipe) compose));
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack out = super.assemble(input, registries);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(getResultItem(RegistryAccess.EMPTY).getItem())) return ItemStack.EMPTY;
            if (stack.getItem() instanceof ArmorItem) {
                out.applyComponents(stack.getComponents());
                break;
            }
        }
        return out;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> original = super.getIngredients();
        ItemStack result = getResultItem(RegistryAccess.EMPTY);

        NonNullList<Ingredient> out = NonNullList.withSize(original.size(), Ingredient.EMPTY);
        for (int i = 0; i < original.size(); i++) {
            out.set(i, new Ingredient(new ExcludeItemIngredient(original.get(i), result.getItemHolder())));
        }
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }



    private static class Serializer implements RecipeSerializer<TagTransferShapedRecipe> {


        @Override
        public MapCodec<TagTransferShapedRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ShapedRecipe.CODEC.fieldOf("shaped_recipe")
                                    .forGetter(TagTransferShapedRecipe::getShapedRecipe)
                    ).apply(instance,TagTransferShapedRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TagTransferShapedRecipe> streamCodec() {
            return StreamCodec.composite(ShapedRecipe.STREAM_CODEC,
                    TagTransferShapedRecipe::getShapedRecipe,
                    TagTransferShapedRecipe::new);
        }
    }

}

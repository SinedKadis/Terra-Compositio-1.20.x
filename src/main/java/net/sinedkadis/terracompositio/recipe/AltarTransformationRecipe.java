package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.TerraCompositio;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AltarTransformationRecipe implements Recipe<RecipeWrapper> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;

    public AltarTransformationRecipe(NonNullList<Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            return false;
        }
        ItemStack containerItem1 = pContainer.getItem(0);
        ItemStack containerItem2 = pContainer.getItem(1);
        if (containerItem1.equals(containerItem2)) return false;
        boolean first = inputs.get(0).test(containerItem1) || inputs.get(0).test(containerItem2);
        boolean second = containerItem1.isEmpty() || containerItem2.isEmpty();
        if (inputs.size() == 2) {
            second = inputs.get(1).test(containerItem1) || inputs.get(1).test(containerItem2);
        }
        return first && second;
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AltarTransformationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "altar_transformation";
    }
    @SuppressWarnings("DataFlowIssue")
    public static class Serializer implements RecipeSerializer<AltarTransformationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = TerraCompositio.modLoc(Type.ID);


        @Override
        public MapCodec<AltarTransformationRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients")
                                    .forGetter(AltarTransformationRecipe::getIngredients),
                            ItemStack.CODEC.fieldOf("result").forGetter(recipe ->
                                    recipe.getResultItem(null))
                    ).apply(instance, AltarTransformationRecipe::new)
            );
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AltarTransformationRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public AltarTransformationRecipe decode(RegistryFriendlyByteBuf buffer) {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < buffer.readVarInt(); i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
                    return new AltarTransformationRecipe(ingredients, output);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, AltarTransformationRecipe value) {
                    buffer.writeVarInt(value.getIngredients().size());
                    for (Ingredient ingredient : value.getIngredients()) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buffer, value.getResultItem(RegistryAccess.EMPTY));
                }
            };
        }
    }
}

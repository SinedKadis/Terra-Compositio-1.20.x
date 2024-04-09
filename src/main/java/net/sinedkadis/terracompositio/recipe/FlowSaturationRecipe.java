package net.sinedkadis.terracompositio.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.TerraCompositio;
import org.jetbrains.annotations.Nullable;

public class FlowSaturationRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> imputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public FlowSaturationRecipe(NonNullList<Ingredient> imputItems, ItemStack output, ResourceLocation id) {
        this.imputItems = imputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            return false;
        }

        return imputItems.get(0).test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    public static class Type implements RecipeType<FlowSaturationRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "flow_saturation";
    }
    public static class Serializer implements RecipeSerializer<FlowSaturationRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(TerraCompositio.MOD_ID,"flow_saturation");
        @Override
        public FlowSaturationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe,"ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1,Ingredient.EMPTY);
            for (int i=0; i < inputs.size();i++){
                inputs.set(i,Ingredient.fromJson(ingredients.get(i)));
            }



            return new FlowSaturationRecipe(inputs,output,pRecipeId);
        }

        @Override
        public @Nullable FlowSaturationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(),Ingredient.EMPTY);
            for (int i = 0; i<inputs.size();i++){
                inputs.set(i,Ingredient.fromNetwork(pBuffer));
            }
            ItemStack output = pBuffer.readItem();
            return new FlowSaturationRecipe(inputs,output,pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FlowSaturationRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.imputItems.size());
            for (Ingredient ingredient:pRecipe.getIngredients()){
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(null),false);
        }
    }
}
package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TechnetiumFiringRecipe implements Recipe<SingleRecipeInput> {
    private final ItemStack furnaceOutputItem;
    private final int ecf;
    private boolean assembled;

    public TechnetiumFiringRecipe(
            ItemStack furnaceOutputItem,
            int ecf) {
        this.furnaceOutputItem = furnaceOutputItem;
        assembled = false;
        this.ecf = ecf;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,Ingredient.of(furnaceOutputItem));
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return furnaceOutputItem.is(input.getItem(0).getItem());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        assembled = true;
        return input.getItem(0);
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return furnaceOutputItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<TechnetiumFiringRecipe>{
        public static final TechnetiumFiringRecipe.Type INSTANCE = new TechnetiumFiringRecipe.Type();
        public static final String ID = "technetium_firing";

        @Override
        public String toString() {
            return ID;
        }
    }
    public static class Serializer implements RecipeSerializer<TechnetiumFiringRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        //public static final ResourceLocation ID = new ResourceLocation(TerraCompositio.MOD_ID,"flow_infusion");

        @Override
        public MapCodec<TechnetiumFiringRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ItemStack.CODEC.fieldOf("furnace_output").forGetter(TechnetiumFiringRecipe::getFurnaceOutputItem),
                            Codec.INT.fieldOf("ecf").forGetter(TechnetiumFiringRecipe::getEcf)
                    ).apply(instance,TechnetiumFiringRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TechnetiumFiringRecipe> streamCodec() {
            return StreamCodec.composite(
                    ItemStack.STREAM_CODEC,TechnetiumFiringRecipe::getFurnaceOutputItem,
                    ByteBufCodecs.INT,TechnetiumFiringRecipe::getEcf,
                    TechnetiumFiringRecipe::new
            );
        }
    }
}

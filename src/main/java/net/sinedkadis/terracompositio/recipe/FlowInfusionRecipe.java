package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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
public class FlowInfusionRecipe implements Recipe<RecipeWrapper> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    @Getter
    private final int ecf;
    @Getter
    private final int ticks;

    public FlowInfusionRecipe(NonNullList<Ingredient> inputItems, ItemStack output, int ecf, int ticks) {
        this.inputItems = inputItems;
        this.output = output;
        this.ecf = ecf;
        this.ticks = ticks;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            return false;
        }

        return inputItems.getFirst().test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public float getECFTick() {
        return (float) ecf / ticks;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    public static class Type implements RecipeType<FlowInfusionRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "flow_infusion";
    }
    public static class Serializer implements RecipeSerializer<FlowInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = TerraCompositio.modLoc("flow_infusion");


        @Override
        public MapCodec<FlowInfusionRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                                    NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients")
                                            .forGetter(FlowInfusionRecipe::getIngredients),
                                    ItemStack.CODEC.fieldOf("result")
                                            .forGetter(recipe -> recipe.getResultItem(RegistryAccess.EMPTY)),
                                    Codec.INT.fieldOf("ecf")
                                            .forGetter(FlowInfusionRecipe::getEcf),
                                    Codec.INT.fieldOf("ticks")
                                            .forGetter(FlowInfusionRecipe::getTicks)
                            )
                            .apply(instance, FlowInfusionRecipe::new)
            );
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlowInfusionRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public FlowInfusionRecipe decode(RegistryFriendlyByteBuf buffer) {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < buffer.readVarInt(); i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
                    int ecf = ByteBufCodecs.VAR_INT.decode(buffer);
                    int tick = ByteBufCodecs.VAR_INT.decode(buffer);
                    return new FlowInfusionRecipe(ingredients, output, ecf, tick);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, FlowInfusionRecipe value) {
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

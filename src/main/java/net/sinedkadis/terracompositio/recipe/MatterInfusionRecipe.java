package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.TerraCompositio;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class MatterInfusionRecipe implements Recipe<RecipeWrapper> {

    @Getter
    private final ItemStack catalyst;
    private final ItemStack input;
    private final ItemStack output;
    @Getter
    private final int catalystDecayRate;
    @Getter
    private final int ecf;
    @Getter
    private final int ticks;

    public MatterInfusionRecipe(ItemStack output, ItemStack catalyst, ItemStack input,  int catalystDecayRate, int ecf, int ticks) {
        this.catalyst = catalyst;
        this.input = input;
        this.output = output;
        this.catalystDecayRate = catalystDecayRate;
        this.ecf = ecf;
        this.ticks = ticks;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            return false;
        }

        ItemStack catalystSlot = pContainer.getItem(0);
        ItemStack inputSlot = pContainer.getItem(1);

        return catalystSlot.is(catalyst.getItem())
                && inputSlot.is(input.getItem())
                && inputSlot.getCount() >= input.getCount();
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(),Ingredient.of(catalyst),Ingredient.of(input));
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
    public static class Type implements RecipeType<MatterInfusionRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "matter_infusion";
    }
    public static class Serializer implements RecipeSerializer<MatterInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.tryBuild(TerraCompositio.MOD_ID,"matter_infusion");

        @Override
        public MapCodec<MatterInfusionRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ItemStack.CODEC.fieldOf("result")
                                    .forGetter(MatterInfusionRecipe::getOutput),
                            ItemStack.CODEC.fieldOf("catalyst")
                                    .forGetter(MatterInfusionRecipe::getCatalyst),
                            ItemStack.CODEC.fieldOf("input")
                                    .forGetter(MatterInfusionRecipe::getInput),
                            Codec.INT.fieldOf("ecf")
                                    .forGetter(MatterInfusionRecipe::getEcf),
                            Codec.INT.fieldOf("ticks")
                                    .forGetter(MatterInfusionRecipe::getTicks),
                            Codec.INT.fieldOf("catalyst_decay")
                                    .forGetter(MatterInfusionRecipe::getCatalystDecayRate)
                    ).apply(instance,MatterInfusionRecipe::new));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MatterInfusionRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public MatterInfusionRecipe decode(RegistryFriendlyByteBuf buffer) {
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
                    ItemStack catalyst = ItemStack.STREAM_CODEC.decode(buffer);
                    ItemStack input = ItemStack.STREAM_CODEC.decode(buffer);
                    int cfe = buffer.readVarInt();
                    int ticks = buffer.readVarInt();
                    int catalystDecayRate = buffer.readVarInt();

                    return new MatterInfusionRecipe(output, catalyst, input, catalystDecayRate, cfe, ticks);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, MatterInfusionRecipe value) {
                    ItemStack.STREAM_CODEC.encode(buffer, value.output);
                    ItemStack.STREAM_CODEC.encode(buffer, value.catalyst);
                    ItemStack.STREAM_CODEC.encode(buffer, value.input);
                    buffer.writeVarInt(value.ecf);
                    buffer.writeVarInt(value.ticks);
                    buffer.writeVarInt(value.catalystDecayRate);
                }
            };
        }
    }
}

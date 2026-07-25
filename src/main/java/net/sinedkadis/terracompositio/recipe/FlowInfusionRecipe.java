package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.particle.ECFParticleData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class FlowInfusionRecipe implements ITCRecipe<RecipeWrapper> {
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

    @Override
    public CraftException allowOnTick(TCBlockEntity be) {
        IItemHandler itemCapability = be.getItemCapability(null);
        ItemStack recipeOutput = getOutput();

        CraftException noSpace = ITCRecipe.checkSpace(itemCapability, recipeOutput);
        if (noSpace.hasExceptions()) return noSpace;


        IECFHandler ecfCapability = be.getECFCapability(null);
        float ecf = getECFTick();

        CraftException noECF = ITCRecipe.checkECF(ecfCapability, ecf);
        if (noECF.hasExceptions()) return noECF;

        CraftException noInfusion = ITCRecipe.checkInfusion(be);
        if (noInfusion.hasExceptions()) return noInfusion;


        return CraftException.OK;
    }

    @Override
    public CraftException allowOnNeighbourUpdate(TCBlockEntity be) {
        CraftException noSurrounding = ITCRecipe.checkSurroundings(be);
        if (noSurrounding.hasExceptions()) return noSurrounding;
        return CraftException.OK;
    }

    @Override
    public void onCraftingTick(TCBlockEntity be, int progress) {
        spawnParticles(be);
        ITCRecipe.consumeECF(be, getECFTick());
        be.setChanged();
    }

    public void spawnParticles(TCBlockEntity be) {
        if (be.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos blockPos = be.getBlockPos();
            serverLevel.sendParticles(new ECFParticleData(1 / 20f),
                    blockPos.getX() + 0.5D,
                    blockPos.getY() + 0.5D,
                    blockPos.getZ() + 0.5D, 1, 0, -0.1D, 0, 0.1D);
        }
    }


    @Override
    public boolean isCompleteThenCraft(TCBlockEntity be, int progress) {
        if (progress > getTicks()) {
            ITCRecipe.craftItem(be, this);
            return true;
        }
        return false;
    }


    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        data.putInt(TooltipHelper.Keys.ECF_CONSUME.toData(), getEcf());
        data.putInt(TooltipHelper.Keys.MAX_PROGRESS.toData(), getTicks());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        if (TCCommonConfigs.DEBUG.get()) {
            TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_PROGRESS, tooltip, data);
        }
    }

    public static class Type implements ITCRecipeType<RecipeWrapper, FlowInfusionRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "flow_infusion";

        @Override
        public RecipeWrapper getRecipeInput(TCBlockEntity be) {
            return new RecipeWrapper(be.getItemCapability(null));
        }
    }
    public static class Serializer implements RecipeSerializer<FlowInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        //public static final ResourceLocation ID = TerraCompositio.modLoc("flow_infusion");


        @Override
        public MapCodec<FlowInfusionRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                                    NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients")
                                            .forGetter(FlowInfusionRecipe::getIngredients),
                                    ItemStack.CODEC.fieldOf("result")
                                            .forGetter(FlowInfusionRecipe::getOutput),
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
                    int i1 = buffer.readVarInt();
                    for (int i = 0; i < i1; i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
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
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, value.getOutput());
                    ByteBufCodecs.VAR_INT.encode(buffer,value.getEcf());
                    ByteBufCodecs.VAR_INT.encode(buffer,value.getTicks());
                }
            };
        }
    }
}

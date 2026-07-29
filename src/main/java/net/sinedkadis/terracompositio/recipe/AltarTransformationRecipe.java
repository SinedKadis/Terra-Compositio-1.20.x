package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class AltarTransformationRecipe implements ITCRecipe<RecipeWrapper> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int maxProgress = 80;

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

    @Override
    public CraftException allowOnTick(TCBlockEntity be) {
        IItemHandler itemCapability = be.getItemCapability(null);
        ItemStack recipeOutput = getOutput();

        CraftException noSpace = ITCRecipe.checkSpace(itemCapability, recipeOutput);
        if (noSpace.hasExceptions()) return noSpace;

        CraftException infusion = ITCRecipe.checkInfusion(be);
        if (infusion.hasExceptions()) return infusion;


        return CraftException.OK;

    }

    @Override
    public CraftException allowOnNeighbourUpdate(TCBlockEntity be) {
        return ITCRecipe.checkPedestal(be);
    }

    @Override
    public void onCraftingTick(TCBlockEntity be, int progress) {
        ITCRecipe.spawnParticles(be);
        if (progress == 1) {
            Level level = be.getLevel();
            if (level != null) {
                level.playSound(null, be.getBlockPos(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS);
            }
        }
        be.setChanged();
    }

    @Override
    public boolean isCompleteThenCraft(TCBlockEntity be, int progress) {
        if (progress > maxProgress) {
            craftItem(be);
            return true;
        }
        return false;
    }

    protected void craftItem(TCBlockEntity blockEntity) {
        ItemStack result = getOutput();
        IItemHandler itemCapability = blockEntity.getItemCapability(null);
        IItemHandlerModifiable capability = (IItemHandlerModifiable) itemCapability;
        capability.setStackInSlot(0, ItemStack.EMPTY);
        capability.setStackInSlot(1, ItemStack.EMPTY);
        capability.setStackInSlot(2, result);
        Level level = blockEntity.getLevel();
        if (level != null)
            level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
        blockEntity.setChanged();
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {

    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        if (TCCommonConfigs.DEBUG.get()) {
            tooltip.add(TooltipHelper.keyWithArg(TooltipHelper.Keys.MAX_PROGRESS, maxProgress));
        }
    }

    public static class Type implements ITCRecipeType<RecipeWrapper, AltarTransformationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "altar_transformation";

        @Override
        public RecipeWrapper getRecipeInput(TCBlockEntity be) {
            return new RecipeWrapper(be.getItemCapability(null));
        }
    }
    public static class Serializer implements RecipeSerializer<AltarTransformationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        //public static final ResourceLocation ID = TerraCompositio.modLoc(Type.ID);


        @Override
        public MapCodec<AltarTransformationRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients")
                                    .forGetter(AltarTransformationRecipe::getIngredients),
                            ItemStack.CODEC.fieldOf("result").forGetter(AltarTransformationRecipe::getOutput)
                    ).apply(instance, AltarTransformationRecipe::new)
            );
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AltarTransformationRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public AltarTransformationRecipe decode(RegistryFriendlyByteBuf buffer) {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    int i1 = buffer.readVarInt();
                    for (int i = 0; i < i1; i++) {
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

package net.sinedkadis.terracompositio.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class AltarTransformationRecipe implements ITCRecipe<RecipeWrapper> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int maxProgress = 80;
    private final ResourceLocation id;

    public AltarTransformationRecipe(NonNullList<Ingredient> inputs, ItemStack output, ResourceLocation id) {
        this.inputs = inputs;
        this.output = output;
        this.id = id;
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
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
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

    @Override
    public CraftException allowOnTick(TCBlockEntity be) {
        IItemHandler itemCapability = be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
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
        ItemStack result = getOutput().copy();
        IItemHandler itemCapability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
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
    public void collectKnowledgeData(CompoundTag data) {

    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        if (TCCommonConfigs.DEBUG.get()) {
            tooltip.add(TooltipHelper.keyWithArg(TooltipHelper.Keys.MAX_PROGRESS, maxProgress));
        }
    }

    public static class Type implements ITCRecipeType<RecipeWrapper, AltarTransformationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "altar_transformation";

        @Override
        public RecipeWrapper getRecipeInput(TCBlockEntity be) {
            return new RecipeWrapper((IItemHandlerModifiable) be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .orElse(SentinelHelper.EMPTY_ITEM_HANDLER));
        }
    }
    @SuppressWarnings("DataFlowIssue")
    public static class Serializer implements RecipeSerializer<AltarTransformationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = TerraCompositio.modLoc(Type.ID);
        @Override
        public AltarTransformationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

            JsonArray ingredients1 = pSerializedRecipe.getAsJsonArray("ingredients");

            NonNullList<Ingredient> ingredients = NonNullList.create();

            ingredients1.asList()
                    .forEach(jsonElement -> ingredients.add(Ingredient.fromJson(jsonElement)));

            JsonObject outputObject = GsonHelper.getAsJsonObject(pSerializedRecipe, "output");
            ResourceLocation itemId = ResourceLocation.tryParse(GsonHelper.getAsString(outputObject, "item"));
            int count = GsonHelper.getAsInt(outputObject, "count", 1);
            ItemStack output = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemId)), count);

            if (outputObject.has("nbt")) {
                try {
                    CompoundTag nbt = TagParser.parseTag(GsonHelper.getAsString(outputObject, "nbt"));
                    output.setTag(nbt);
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT tag in recipe output", e);
                }
            }

            return new AltarTransformationRecipe(ingredients, output, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AltarTransformationRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.inputs.size());
            for (Ingredient ingredient : pRecipe.inputs) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }

        @Override
        public @Nullable AltarTransformationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < pBuffer.readVarInt(); i++) {
                ingredients.add(Ingredient.fromNetwork(pBuffer));
            }
            ItemStack output = pBuffer.readItem();
            return new AltarTransformationRecipe(ingredients, output, pRecipeId);
        }


    }
}

package net.sinedkadis.terracompositio.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class FlowInfusionRecipe implements ITCRecipe<RecipeWrapper> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;
    @Getter
    private final int ecf;
    @Getter
    private final int ticks;

    public FlowInfusionRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id, int ecf, int ticks) {
        this.inputItems = inputItems;
        this.output = output;
        this.ecf = ecf;
        this.ticks = ticks;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        if(pLevel.isClientSide()){
            return false;
        }

        return inputItems.get(0).test(pContainer.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, RegistryAccess access) {
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

    public float getECFTick() {
        return (float) ecf / ticks;
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


        IECFHandler ecfCapability = be.getCapability(TCCapabilities.ECF)
                .orElse(SentinelHelper.EMPTY_ECF_HANDLER);
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
    public void collectKnowledgeData(CompoundTag data) {
        data.putInt(TooltipHelper.Keys.ECF_CONSUME.toData(), getEcf());
        data.putInt(TooltipHelper.Keys.MAX_PROGRESS.toData(), getTicks());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        if (TCCommonConfigs.DEBUG.get()) {
            TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_PROGRESS, tooltip, data);
        }
    }

    public static class Type implements ITCRecipeType<RecipeWrapper, FlowInfusionRecipe> {
        public static final ITCRecipeType<RecipeWrapper, FlowInfusionRecipe> INSTANCE = new Type();
        public static final String ID = "flow_infusion";

        @Override
        public RecipeWrapper getRecipeInput(TCBlockEntity be) {
            return new RecipeWrapper((IItemHandlerModifiable) be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .orElse(SentinelHelper.EMPTY_ITEM_HANDLER));
        }
    }
    public static class Serializer implements RecipeSerializer<FlowInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = TerraCompositio.modLoc("flow_infusion");
        @Override
        public FlowInfusionRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int cfe = GsonHelper.getAsInt(pSerializedRecipe, "ecf");
            int ticks = GsonHelper.getAsInt(pSerializedRecipe,"time");
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe,"ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1,Ingredient.EMPTY);
            for (int i=0; i < inputs.size();i++){
                inputs.set(i,Ingredient.fromJson(ingredients.get(i)));
            }



            return new FlowInfusionRecipe(inputs,output,pRecipeId,cfe,ticks);
        }

        @Override
        public @Nullable FlowInfusionRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(),Ingredient.EMPTY);
            inputs.replaceAll(ignored -> Ingredient.fromNetwork(pBuffer));
            ItemStack output = pBuffer.readItem();
            int cfe = pBuffer.readInt();
            int ticks = pBuffer.readInt();
            return new FlowInfusionRecipe(inputs,output,pRecipeId,cfe,ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FlowInfusionRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());
            for (Ingredient ingredient:pRecipe.getIngredients()){
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getOutput(), false);
            pBuffer.writeInt(pRecipe.ecf);
            pBuffer.writeInt(pRecipe.ticks);
        }
    }
}

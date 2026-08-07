package net.sinedkadis.terracompositio.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity;
import net.sinedkadis.terracompositio.block.entity.MatterInfuserPortBlockEntity;
import net.sinedkadis.terracompositio.block.entity.MatterInfuserUnitBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class MatterInfusionRecipe implements ITCRecipe<RecipeWrapper> {

    @Getter
    private final ItemStack catalyst;
    private final ItemStack input;
    private final ItemStack output;
    private final ResourceLocation id;
    @Getter
    private final int catalystDecayRate;
    @Getter
    private final int ecf;
    @Getter
    private final int ticks;

    public MatterInfusionRecipe(ItemStack output, ItemStack catalyst, ItemStack input, int catalystDecayRate, ResourceLocation pRecipeId, int ecf, int ticks) {
        this.catalyst = catalyst;
        this.input = input;
        this.output = output;
        this.catalystDecayRate = catalystDecayRate;
        this.ecf = ecf;
        this.ticks = ticks;
        this.id = pRecipeId;
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
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(),Ingredient.of(catalyst),Ingredient.of(input));
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
        if (!(be instanceof MatterInfuserUnitBlockEntity miBE)) throw new AssertionError();
        FlowCedarCasingBlockEntity casingBE = miBE.getCasingBE();

        CraftException noSurroundings = ITCRecipe.checkInfusion(casingBE);
        if (noSurroundings.hasExceptions()) return noSurroundings;

        assert casingBE != null;

        IItemHandler itemCapability = casingBE.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
        ItemStack recipeOutput = getOutput();

        CraftException noSpace = ITCRecipe.checkSpace(itemCapability, recipeOutput);
        if (noSpace.hasExceptions()) return noSpace;


        IECFHandler ecfCapability = be.getCapability(TCCapabilities.ECF)
                .orElse(SentinelHelper.EMPTY_ECF_HANDLER);
        float ecf = getECFTick();

        CraftException noECF = ITCRecipe.checkECF(ecfCapability, ecf);
        if (noECF.hasExceptions()) return noECF;

        if (!miBE.assembleValid()) return CraftException.NO_SURROUNDINGS;

        return CraftException.OK;
    }

    @Override
    public void onCraftingTick(TCBlockEntity be, int progress) {
        Level level = be.getLevel();
        if (level == null) return;
        long gameTime = level.getGameTime();
        if ((gameTime % 20) == 0) {
            ParticleHelperInternal.spawnParticlesIn(level,
                    be.getBlockPos().relative(be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite()),
                    ((int) Math.ceil(getECFTick() * 20)));
        }
        if ((gameTime % 10) == 5) {
            level.playSound(null, be.getBlockPos(), SoundEvents.AZALEA_STEP, SoundSource.BLOCKS);
        }

        ITCRecipe.consumeECF(be, getECFTick());
        be.setChanged();
    }

    @Override
    public boolean isCompleteThenCraft(TCBlockEntity be, int progress) {
        if (progress > getTicks() && be instanceof MatterInfuserUnitBlockEntity miBE) {
            craftItem(miBE);
            return true;
        }
        return false;
    }

    protected void craftItem(MatterInfuserUnitBlockEntity be) {
        MatterInfuserPortBlockEntity portBE = be.getPortBE();
        FlowCedarCasingBlockEntity casingBE = be.getCasingBE();
        Level level = be.getLevel();
        if (level != null
                && portBE != null
                && casingBE != null) {
            IItemHandler iItemHandler = casingBE.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
            iItemHandler.getStackInSlot(0).shrink(this.input.getCount());
            ItemStack stackInSlot = iItemHandler.getStackInSlot(1).copy();
            if (stackInSlot.isEmpty()) stackInSlot = this.output.copy();
            else stackInSlot.grow(this.output.getCount());
            ((IItemHandlerModifiable) iItemHandler).setStackInSlot(1, stackInSlot);
            BlockState blockState = be.getBlockState();
            level.sendBlockUpdated(be.getBlockPos(), blockState, blockState, 3);
            if (level.getRandom().nextInt(100) < catalystDecayRate) {
                portBE.extractItemStackViaSetter(0, 1);
            }
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data) {
        data.putInt(TooltipHelper.Keys.ECF_CONSUME.toData(), getEcf());
        data.putInt(TooltipHelper.Keys.MAX_PROGRESS.toData(), getTicks());
        data.putInt(TooltipHelper.Keys.DECAY_RATE.toData(), getCatalystDecayRate());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        if (TCCommonConfigs.DEBUG.get()) {
            TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_PROGRESS, tooltip, data);
        }
        TooltipHelper.addIfExist(TooltipHelper.Keys.DECAY_RATE, tooltip, data);
    }

    public static class Type implements ITCRecipeType<RecipeWrapper, MatterInfusionRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "matter_infusion";

        @Override
        public RecipeWrapper getRecipeInput(TCBlockEntity be) {
            if (be instanceof MatterInfuserUnitBlockEntity miBE) {
                ItemStack catalyst = miBE.getCatalyst();
                ItemStack inputSlot = miBE.getInputSlot();
                if (catalyst.isEmpty() || inputSlot.isEmpty())
                    return new RecipeWrapper((IItemHandlerModifiable) SentinelHelper.EMPTY_ITEM_HANDLER);
                IItemHandlerModifiable inventory = new InvWrapper(new SimpleContainer(catalyst, inputSlot));
                return new RecipeWrapper(inventory);
            }
            return new RecipeWrapper((IItemHandlerModifiable) SentinelHelper.EMPTY_ITEM_HANDLER);
        }
    }
    public static class Serializer implements RecipeSerializer<MatterInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.tryBuild(TerraCompositio.MOD_ID,"matter_infusion");
        @Override
        public MatterInfusionRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            String catalystStr = GsonHelper.getAsString(pSerializedRecipe, "catalyst");
            Item catalyst = ForgeRegistries.ITEMS.getDelegateOrThrow(ResourceLocation.tryParse(catalystStr)).get();
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            int cfe = GsonHelper.getAsInt(pSerializedRecipe, "ecf");
            int ticks = GsonHelper.getAsInt(pSerializedRecipe,"time");
            int catalystDecayRate = GsonHelper.getAsInt(pSerializedRecipe,"rate");

            return new MatterInfusionRecipe(output, catalyst.getDefaultInstance(), input, catalystDecayRate, pRecipeId, cfe, ticks);
        }

        @Override
        public @Nullable MatterInfusionRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();
            ItemStack catalyst = pBuffer.readItem();
            ItemStack input = pBuffer.readItem();
            int cfe = pBuffer.readInt();
            int ticks = pBuffer.readInt();
            int catalystDecayRate = pBuffer.readInt();

            return new MatterInfusionRecipe(output, catalyst, input, catalystDecayRate, pRecipeId, cfe, ticks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MatterInfusionRecipe pRecipe) {
            pBuffer.writeItemStack(pRecipe.output,false);
            pBuffer.writeItemStack(pRecipe.catalyst, true);
            pBuffer.writeItemStack(pRecipe.input,false);
            pBuffer.writeInt(pRecipe.ecf);
            pBuffer.writeInt(pRecipe.ticks);
            pBuffer.writeInt(pRecipe.catalystDecayRate);
        }
    }
}

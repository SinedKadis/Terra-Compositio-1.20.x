package net.sinedkadis.terracompositio.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity;
import net.sinedkadis.terracompositio.block.entity.MatterInfuserPortBlockEntity;
import net.sinedkadis.terracompositio.block.entity.MatterInfuserUnitBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;

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

    @Override
    public CraftException allowOnTick(TCBlockEntity be) {
        if (!(be instanceof MatterInfuserUnitBlockEntity miBE)) throw new AssertionError();
        FlowCedarCasingBlockEntity casingBE = miBE.getCasingBE();

        CraftException noSurroundings = ITCRecipe.checkInfusion(casingBE);
        if (noSurroundings.hasExceptions()) return noSurroundings;

        assert casingBE != null;

        IItemHandler itemCapability = casingBE.getItemCapability(null);
        ItemStack recipeOutput = getOutput();

        CraftException noSpace = ITCRecipe.checkSpace(itemCapability, recipeOutput);
        if (noSpace.hasExceptions()) return noSpace;


        IECFHandler ecfCapability = be.getECFCapability(null);
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
            ITCRecipe.craftItem(be, this);
            BlockState blockState = be.getBlockState();
            level.sendBlockUpdated(be.getBlockPos(), blockState, blockState, 3);
            if (level.getRandom().nextInt(100) < catalystDecayRate) {
                portBE.extractItemStackViaSetter(0, 1);
            }
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        data.putInt(TooltipHelper.Keys.ECF_CONSUME.toData(), getEcf());
        data.putInt(TooltipHelper.Keys.MAX_PROGRESS.toData(), getTicks());
        data.putInt(TooltipHelper.Keys.DECAY_RATE.toData(), getCatalystDecayRate());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
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
                    return new RecipeWrapper(SentinelHelper.EMPTY_ITEM_HANDLER);
                IItemHandler inventory = new InvWrapper(new SimpleContainer(catalyst, inputSlot));
                return new RecipeWrapper(inventory);
            }
            return new RecipeWrapper(SentinelHelper.EMPTY_ITEM_HANDLER);
        }
    }
    public static class Serializer implements RecipeSerializer<MatterInfusionRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        //public static final ResourceLocation ID = ResourceLocation.tryBuild(TerraCompositio.MOD_ID,"matter_infusion");

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
                            Codec.INT.fieldOf("catalyst_decay")
                                    .forGetter(MatterInfusionRecipe::getCatalystDecayRate),
                            Codec.INT.fieldOf("ecf")
                                    .forGetter(MatterInfusionRecipe::getEcf),
                            Codec.INT.fieldOf("ticks")
                                    .forGetter(MatterInfusionRecipe::getTicks)
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
                    int catalystDecayRate = buffer.readVarInt();
                    int cfe = buffer.readVarInt();
                    int ticks = buffer.readVarInt();

                    return new MatterInfusionRecipe(output, catalyst, input, catalystDecayRate, cfe, ticks);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, MatterInfusionRecipe value) {
                    ItemStack.STREAM_CODEC.encode(buffer, value.output);
                    ItemStack.STREAM_CODEC.encode(buffer, value.catalyst);
                    ItemStack.STREAM_CODEC.encode(buffer, value.input);
                    buffer.writeVarInt(value.catalystDecayRate);
                    buffer.writeVarInt(value.ecf);
                    buffer.writeVarInt(value.ticks);

                }
            };
        }
    }
}

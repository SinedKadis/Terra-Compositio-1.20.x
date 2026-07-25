package net.sinedkadis.terracompositio.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties.INFUSED;

public interface ITCRecipe<INPUT extends Container> extends Recipe<INPUT>, IHaveKnowledge {


    static void consumeECF(TCBlockEntity be, float ecfTick) {
        Level level = be.getLevel();
        if (level == null) return;

        int floorECF = (int) Math.floor(ecfTick);
        float fractional = ecfTick - floorECF;

        int amount = floorECF;
        if (fractional > 0f) {
            int period = Math.round(1f / fractional);
            if (period > 0 && level.getGameTime() % period == 0) {
                amount += 1;
            }
        }

        IECFHandler capability = be.getCapability(TCCapabilities.ECF)
                .orElse(SentinelHelper.EMPTY_ECF_HANDLER);
        capability.takeECF(amount, TransferAction.EXECUTE);
    }

    static void craftItem(TCBlockEntity be, ITCRecipe<? extends Container> recipe) {
        Level level = be.getLevel();
        if (level != null) {
            IItemHandler iItemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
            if (iItemHandler instanceof IItemHandlerModifiable modifiable) {
                NonNullList<Ingredient> ingredients = recipe.getIngredients();

                for (int i = 0; i < iItemHandler.getSlots() - 1; i++) { //ignore last slot
                    ItemStack copy = modifiable.getStackInSlot(0).copy();
                    int count = -1;
                    for (ItemStack stack : ingredients.get(i).getItems()) {
                        if (stack.is(copy.getItem())) {
                            count = stack.getCount();
                            break;
                        }
                    }
                    if (count == -1) throw new IllegalArgumentException("ItemStack " + copy + " not found in " +
                            Arrays.toString(ingredients.get(i).getItems()));
                    copy.shrink(count);
                    modifiable.setStackInSlot(i, copy);
                }

                int lastSlot = iItemHandler.getSlots() - 1;
                ItemStack resultItem = recipe.getResultItem(level.registryAccess());
                ItemStack lastItem = iItemHandler.getStackInSlot(lastSlot).copy();

                if (lastItem.isEmpty()) {
                    lastItem = resultItem;
                } else {
                    if (!lastItem.is(resultItem.getItem())) {
                        throw new IllegalArgumentException("Last slot is not output slot - " + iItemHandler);
                    }
                    lastItem.grow(resultItem.getCount());
                }

                modifiable.setStackInSlot(lastSlot, lastItem);
                BlockState blockState = be.getBlockState();
                level.sendBlockUpdated(be.getBlockPos(), blockState, blockState, 3);
                be.setChanged();
            }
        }
    }

    static void spawnParticles(TCBlockEntity be) {
        ParticleHelperInternal.spawnParticlesIn(be.getLevel(), be.getBlockPos());
    }

    static CraftException checkECF(IECFHandler ecfCapability, float ecf) {
        return ecfCapability.getECF() < ecf ? CraftException.NO_ECF : CraftException.OK;
    }

    static CraftException checkSurroundings(TCBlockEntity be) {
        if (be.getLevel() != null) {
            return WorldHelperInternal.surroundedByFlow(be.getLevel(), be.getBlockPos());
        }
        return CraftException.OK;
    }

    static CraftException checkSpace(IItemHandler itemCapability, ItemStack recipeOutput) {
        int lastSlot = itemCapability.getSlots() - 1;
        ItemStack outputSlot = itemCapability.getStackInSlot(lastSlot);

        if (!outputSlot.isEmpty() && !outputSlot.is(recipeOutput.getItem()))
            return CraftException.NO_SPACE;
        if (outputSlot.getCount() + recipeOutput.getCount() >
                Math.min(
                        outputSlot.getMaxStackSize(),
                        itemCapability.getSlotLimit(lastSlot)
                ))
            return CraftException.NO_SPACE;
        return CraftException.OK;
    }

    static CraftException checkInfusion(@Nullable TCBlockEntity casingBE) {
        if (casingBE == null) return CraftException.NO_SURROUNDINGS;
        if (!casingBE.getBlockState().getValue(INFUSED)) return CraftException.NO_SURROUNDINGS;
        return CraftException.OK;
    }

    static CraftException checkPedestal(TCBlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return CraftException.NO_SURROUNDINGS;

        BlockPos belowPos = be.getBlockPos().below();
        BlockState belowState = level.getBlockState(belowPos);

        if (!belowState.is(TCBlocks.FLOW_CEDAR_PEDESTAL.get())) return CraftException.NO_SURROUNDINGS;
        return CraftException.OK;
    }

    CraftException allowOnTick(TCBlockEntity be);

    default CraftException allowOnNeighbourUpdate(TCBlockEntity be) {
        return CraftException.OK;
    }

    void onCraftingTick(TCBlockEntity be, int progress);

    boolean isCompleteThenCraft(TCBlockEntity be, int progress);

    enum CraftException implements TooltipHelper.ICustomUnit {
        OK,
        NO_RECIPE,
        NO_ECF,
        NO_SPACE,
        NO_SURROUNDINGS;

        public boolean hasExceptions() {
            return this != OK;
        }

        @Override
        public String getModID() {
            return TerraCompositioAPI.MOD_ID;
        }
    }
}

package net.sinedkadis.terracompositio.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
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
import net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;
import org.jetbrains.annotations.Nullable;

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

    static void craftItem(TCBlockEntity be, ItemStack result) {
        Level level = be.getLevel();
        if (level != null) {
            IItemHandler iItemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
            if (iItemHandler instanceof IItemHandlerModifiable modifiable) {
                ItemStack copy = modifiable.getStackInSlot(0).copy();
                copy.shrink(1);
                modifiable.setStackInSlot(0, copy);
                modifiable.setStackInSlot(1, result.copy());
                BlockState blockState = be.getBlockState();
                level.sendBlockUpdated(be.getBlockPos(), blockState, blockState, 3);
                be.setChanged();
            }
        }
    }

    static void spawnParticles(TCBlockEntity be) {
        if (be.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos blockPos = be.getBlockPos();
            serverLevel.sendParticles(new ECFParticleData(1 / 20f),
                    blockPos.getX() + 0.5D,
                    blockPos.getY() + 0.5D,
                    blockPos.getZ() + 0.5D, 1, 0, -0.1D, 0, 0.1D);
        }
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

        if (!outputSlot.is(recipeOutput.getItem()))
            return CraftException.NO_SPACE;
        if (outputSlot.getCount() + recipeOutput.getCount() >
                Math.min(
                        outputSlot.getMaxStackSize(),
                        itemCapability.getSlotLimit(lastSlot)
                ))
            return CraftException.NO_SPACE;
        return CraftException.OK;
    }

    static CraftException checkInfusion(@Nullable FlowCedarCasingBlockEntity casingBE) {
        if (casingBE == null) return CraftException.NO_SURROUNDINGS;
        if (!casingBE.getBlockState().getValue(INFUSED)) return CraftException.NO_SURROUNDINGS;
        return CraftException.OK;
    }

    CraftException canBeProcessed(TCBlockEntity be);

    void onCraftingTick(TCBlockEntity be);

    boolean onComplete(TCBlockEntity be, int progress);

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

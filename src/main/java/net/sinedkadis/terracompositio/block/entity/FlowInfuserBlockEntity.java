package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import net.sinedkadis.terracompositio.recipe.FlowInfusionRecipe;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowInfuserBlockEntity extends TCCraftingBlockEntity {

    public FlowInfuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TCBlockEntities.FLOW_INFUSER_BE.get(),pPos, pBlockState);
    }

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {
        list.add(new ECFHandlerBehaviour(this)
                .priority(TCInnerConfig.DEFAULT_CONSUMER_PRIORITY));
        list.add(new ItemHandlerBehaviour(this, 2) {
            @Override
            public boolean allowInsert(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
                return manual && pSlot == 0;
            }

            @Override
            public boolean allowExtract(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
                return manual;
            }

            @Override
            public int getLimitInSlot(int slot) {
                return 1;
            }
        });

    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        if (!pLevel.isClientSide) {
            if (hasRecipe() && enoughECF()) {
                increaseCraftingProgress();
                consumeECF();
                setChanged(pLevel, pPos, pState);
                spawnParticles();
                if (hasProgressFinished()) {
                    craftItem();
                    resetProgress();
                }
            } else if (!hasRecipe()) {
                resetProgress();
            }
        }
    }

    public boolean hasRecipe() {
        Optional<RecipeHolder<FlowInfusionRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()){
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(RegistryAccess.EMPTY);
        boolean outputTest = enoughSpaceInOutput(result.getCount()) && sameItemInOutput(result.getItem());
        if (outputTest){
            maxProgress = recipe.get().value().getTicks();
            tickECFCost = recipe.get().value().getECFTick();
        }
        return outputTest;
    }

    protected Optional<RecipeHolder<FlowInfusionRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager().getRecipeFor(FlowInfusionRecipe.Type.INSTANCE, new RecipeWrapper(getItemHandler()), level);
    }

    @Override
    protected int getECF() {
        return ecfContainer().getECF();
    }

    @Override
    protected IItemHandlerModifiable getItemHandler() {
        if (level != null) {
            IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition, null);
            if (capability != null)
                return (IItemHandlerModifiable) capability;
        }
        return (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
    }

    protected IECFHandler ecfContainer() {
        return ((ECFHandlerBehaviour) behaviours.getFirst()).getMainHandler();
    }


    private void spawnParticles() {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos blockPos = getBlockPos();
            serverLevel.sendParticles(new ECFParticleData(1 / 20f),
                    blockPos.getX() + 0.5D,
                    blockPos.getY() + 0.5D,
                    blockPos.getZ() + 0.5D, 1, 0, -0.1D, 0, 0.1D);
        }
    }


    protected void craftItem() {
        Optional<RecipeHolder<FlowInfusionRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && level != null) {
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
            IItemHandler iItemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition, null);
            if (iItemHandler instanceof IItemHandlerModifiable modifiable) {
                ItemStack copy = modifiable.getStackInSlot(0).copy();
                copy.shrink(1);
                modifiable.setStackInSlot(0, copy);
                modifiable.setStackInSlot(1, result.copy());
                if (level != null) {
                    BlockState blockState = getBlockState();
                    level.sendBlockUpdated(worldPosition, blockState, blockState, 3);
                }
            }

        }
    }



}

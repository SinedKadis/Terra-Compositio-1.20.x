package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour;
import net.sinedkadis.terracompositio.recipe.AltarTransformationRecipe;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

import static net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties.INFUSED;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarAltarBlockEntity extends TCCraftingBlockEntity implements IFluidApplicable {


    public FlowCedarAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TCBlockEntities.FLOW_ALTAR_BE.get(), pPos, pBlockState);
    }

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {
        list.add(new ItemHandlerBehaviour(this, 3) {
            @Override
            public int getLimitInSlot(int slot) {
                return 1;
            }

            @Override
            public boolean allowInsert(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
                return !(pSlot == 2);
            }
        });
    }

    boolean wasCrafting = false;
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel,pPos,pState);
        if (this.getBlockState().getValue(INFUSED)
                && pLevel.getBlockState(pPos.below()).is(TCBlocks.FLOW_CEDAR_PEDESTAL.get())) {
            if (hasRecipe()) {
                if (!wasCrafting) {
                    pLevel.playSound(null, pPos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS);
                }
                ParticleHelperInternal.spawnParticlesIn(pLevel, pPos);
                increaseCraftingProgress();
                if (hasProgressFinished()) {
                    craftItem();
                    resetProgress();
                    setChanged(pLevel, pPos, pState);
                    pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                }
                wasCrafting = true;
            } else {
                wasCrafting = false;
                resetProgress();
            }
        }
    }

    protected IItemHandlerModifiable getItemHandler() {
        Level level1 = this.level;
        if (level1 == null) return (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
        return (IItemHandlerModifiable) level1
                .getCapability(Capabilities.ItemHandler.BLOCK, this.worldPosition, null);
    }

    protected boolean hasRecipe() {
        Optional<RecipeHolder<AltarTransformationRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()){
            return false;
        }
        if (level == null) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());

        boolean outputTest = enoughSpaceInOutput(result.getCount()) && sameItemInOutput(result.getItem());
        if (outputTest){
            maxProgress = 80;
        }
        return outputTest;
    }

    protected void craftItem() {
        Optional<RecipeHolder<AltarTransformationRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().getResultItem(RegistryAccess.EMPTY);
            this.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            this.getItemHandler().setStackInSlot(1, ItemStack.EMPTY);
            this.getItemHandler().setStackInSlot(2, result);
        }
    }

    protected Optional<RecipeHolder<AltarTransformationRecipe>> getCurrentRecipe() {

        assert this.level != null;
        return this.level.getRecipeManager().getRecipeFor(AltarTransformationRecipe.Type.INSTANCE, new RecipeWrapper(this.getItemHandler()), level);
    }

    @Override
    protected int getECF() {
        return 0;
    }

    @Override
    public int defaultConsumeAmount() {
        return 500;
    }
}

package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.block.behaviours.AssemblyBehaviour;
import net.sinedkadis.terracompositio.block.behaviours.CraftingBehaviour;
import net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour;
import net.sinedkadis.terracompositio.recipe.AltarTransformationRecipe;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarAltarBlockEntity extends TCBlockEntity implements IFluidApplicable {

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
        list.add(new AssemblyBehaviour(FlowCedarAltarBlockEntity.this) {
            @Override
            protected boolean isAssembled(ServerLevel level1) {
                return !ITCRecipe.checkPedestal(FlowCedarAltarBlockEntity.this).hasExceptions();
            }
        });
        list.add(new CraftingBehaviour<>(this, AltarTransformationRecipe.Type.INSTANCE));
    }
}

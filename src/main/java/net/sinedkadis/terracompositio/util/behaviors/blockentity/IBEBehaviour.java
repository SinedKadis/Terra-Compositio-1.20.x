package net.sinedkadis.terracompositio.util.behaviors.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBEBehaviour {
    default void tick() {
    }

    default void onRemoved() {
    }


    //Serialisation
    void onSave(CompoundTag compoundTag, HolderLookup.Provider registries);

    void onLoad(CompoundTag compoundTag, HolderLookup.Provider registries);

    //Block events
    default InteractionResult onUse(@NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit){
        return InteractionResult.PASS;
    }

    default void onNeighbourUpdated(BlockState state,
                                    Direction direction,
                                    BlockState neighborState,
                                    LevelAccessor level,
                                    BlockPos pos,
                                    BlockPos neighborPos) {
    }


    //New 1.21.1 caps
    default IItemHandler getItemCapability(@Nullable Direction direction) {
        return EmptyItemHandler.INSTANCE;
    }

    default IFluidHandler getFluidCapability(@Nullable Direction ignoredDirection) {
        return EmptyFluidHandler.INSTANCE;
    }

    default IECFHandler getECFCapability(@Nullable Direction direction) {
        return SentinelHelper.EMPTY_ECF_HANDLER;
    }

    default IItemHandler getStateHolderCapability(@Nullable Direction direction) {
        return EmptyItemHandler.INSTANCE;
    }
}

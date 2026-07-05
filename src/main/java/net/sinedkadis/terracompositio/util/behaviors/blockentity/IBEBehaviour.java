package net.sinedkadis.terracompositio.util.behaviors.blockentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public interface IBEBehaviour {
    void tick();
    void onChunkLoad();

    //@Nullable LazyOptional<?> getCapability(@NotNull Capability<?> cap, @Nullable Direction side);
    void onRemoved();

    //void onInvalidateCaps();
    //Serialisation
    void onSave(CompoundTag compoundTag, HolderLookup.Provider registries);

    void onLoad(CompoundTag compoundTag, HolderLookup.Provider registries);

    //Block events
    default InteractionResult onUse(@NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit){
        return InteractionResult.PASS;
    }

}

package net.sinedkadis.terracompositio.fluid;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetworkMember;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TCFluidTank extends FluidTank {
    private final BlockEntity blockEntity;

    public TCFluidTank(int size, FluidNetworkMember blockEntity) {
        super(size);

        this.blockEntity = blockEntity.getEntityInstance().tc$asBE();
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        blockEntity.setChanged();
        Level level = blockEntity.getLevel();
        if (level != null && !level.isClientSide()) {
            BlockState blockState = blockEntity.getBlockState();
            level.sendBlockUpdated(blockEntity.getBlockPos(), blockState, blockState, 3);
        }
        if (blockEntity instanceof FluidNetworkMember fluidNetworkMember) {
            TerraCompositioAPI.INSTANCE.getFluidNetworkInstance().fireFluidNetworkEvent(fluidNetworkMember, NetworkAction.UPDATE);
        }
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        nbt.put("Fluid", fluid.saveOptional(lookupProvider));
        return nbt;
    }
}

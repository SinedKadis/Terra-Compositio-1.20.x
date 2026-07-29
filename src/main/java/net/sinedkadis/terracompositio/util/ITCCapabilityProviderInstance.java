package net.sinedkadis.terracompositio.util;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.EmptyEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import org.jetbrains.annotations.Nullable;

public interface ITCCapabilityProviderInstance {
    default IItemHandler getItemCapability(@Nullable Direction direction) {
        return EmptyItemHandler.INSTANCE;
    }

    default IECFHandler getECFCapability(@Nullable Direction direction) {
        return SentinelHelper.EMPTY_ECF_HANDLER;
    }

    default IItemHandler getStateHolderCapability(@Nullable Direction direction) {
        return EmptyItemHandler.INSTANCE;
    }

    default IFluidHandler getFluidCapability(@Nullable Direction direction) {
        return EmptyFluidHandler.INSTANCE;
    }

    default IEnergyStorage getEnergyCapability(@Nullable Direction direction) {
        return EmptyEnergyStorage.INSTANCE;
    }
}

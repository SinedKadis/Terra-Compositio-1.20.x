package net.sinedkadis.terracompositio.util;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import org.jetbrains.annotations.Nullable;

public interface ITCCapabilityProviderInstance {
    IItemHandler getItemCapability(@Nullable Direction direction);

    IECFHandler getECFCapability(@Nullable Direction direction);

    IItemHandler getStateHolderCapability(@Nullable Direction direction);
}

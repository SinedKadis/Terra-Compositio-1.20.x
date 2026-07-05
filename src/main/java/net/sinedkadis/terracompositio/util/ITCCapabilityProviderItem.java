package net.sinedkadis.terracompositio.util;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import org.jetbrains.annotations.Nullable;

public interface ITCCapabilityProviderItem {

    IECFHandler addECFCapability(ItemStack itemStack);

}

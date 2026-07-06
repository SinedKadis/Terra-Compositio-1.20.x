package net.sinedkadis.terracompositio.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;

public interface ITCCapabilityProviderItem {

    default IECFHandler addECFCapability(ItemStack itemStack) {
        return SentinelHelper.EMPTY_ECF_HANDLER;
    }
    default IFluidHandlerItem addFluidCapability(ItemStack itemStack) {
        return (IFluidHandlerItem) EmptyFluidHandler.INSTANCE;
    }

}

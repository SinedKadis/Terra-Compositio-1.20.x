package net.sinedkadis.terracompositio.api.helpers;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.sinedkadis.terracompositio.api.dummies.DummyECFHandler;
import net.sinedkadis.terracompositio.api.dummies.DummyFluidHandlerItem;
import net.sinedkadis.terracompositio.api.dummies.DummyNetwork;

import java.util.UUID;

public class SentinelHelper {
    public static final BlockPos EMPTY_POS = BlockPos.ZERO.atY(-64);
    public static final UUID EMPTY_UUID = UUID.randomUUID();
    public static final DummyECFHandler EMPTY_ECF_HANDLER = new DummyECFHandler();
    public static final DummyNetwork EMPTY_NETWORK = new DummyNetwork();
    public static final IFluidHandlerItem EMPTY_FLUID_HANDLER_ITEM = new DummyFluidHandlerItem();

}

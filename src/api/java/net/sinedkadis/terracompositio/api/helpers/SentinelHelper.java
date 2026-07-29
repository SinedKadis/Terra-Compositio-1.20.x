package net.sinedkadis.terracompositio.api.helpers;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.energy.EmptyEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.sinedkadis.terracompositio.api.dummies.*;

import java.util.UUID;

public class SentinelHelper {
    public static final BlockPos EMPTY_POS = BlockPos.ZERO.atY(-64);
    public static final UUID EMPTY_UUID = UUID.randomUUID();
    public static final DummyNetwork EMPTY_NETWORK = new DummyNetwork();
    public static final DummyEntityInstance EMPTY_ENTITY = new DummyEntityInstance();
    public static final DummyNetworkMember EMPTY_MEMBER = new DummyNetworkMember();
    public static final IFluidHandlerItem EMPTY_FLUID_HANDLER_ITEM = new DummyFluidHandlerItem();


    public static final DummyECFHandler EMPTY_ECF_HANDLER = new DummyECFHandler();
    public static final IEnergyStorage EMPTY_ENERGY_HANDLER = EmptyEnergyStorage.INSTANCE;
    public static final IFluidHandler EMPTY_FLUID_HANDLER = EmptyFluidHandler.INSTANCE;
    public static final IItemHandler EMPTY_ITEM_HANDLER = EmptyItemHandler.INSTANCE;
}

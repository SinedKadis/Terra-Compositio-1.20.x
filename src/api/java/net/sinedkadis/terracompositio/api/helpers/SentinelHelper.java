package net.sinedkadis.terracompositio.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraftforge.energy.EmptyEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
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
    public static final IItemHandler EMPTY_ITEM_HANDLER = EmptyHandler.INSTANCE;
}

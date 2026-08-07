package net.sinedkadis.terracompositio.api.dummies;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetworkMember;

public class DummyNetworkMember implements ECFNetworkMember, FluidNetworkMember {
    @Override
    public IECFHandler getECFHandler() {
        return SentinelHelper.EMPTY_ECF_HANDLER;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return EmptyFluidHandler.INSTANCE;
    }

    @Override
    public void updateIfScheduled() {

    }

    @Override
    public void scheduleMemberUpdate() {

    }

    @Override
    public IEntityInstance getEntityInstance() {
        return SentinelHelper.EMPTY_ENTITY;
    }

    @Override
    public int getRange(boolean inner) {
        return 0;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}

package net.sinedkadis.terracompositio.ecf;

import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;

public class LimitlessDefaultECFHandler extends DefaultECFHandler {
    public LimitlessDefaultECFHandler(ECFNetworkMember entity) {
        super(entity);
    }

    @Override
    public int getFreeSpace() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxECF() {
        return Integer.MAX_VALUE;
    }
}

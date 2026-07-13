package net.sinedkadis.terracompositio.ecf;

import net.sinedkadis.terracompositio.api.IEntityInstance;

public class LimitlessDefaultECFHandler extends DefaultECFHandler {
    public LimitlessDefaultECFHandler(IEntityInstance entity) {
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

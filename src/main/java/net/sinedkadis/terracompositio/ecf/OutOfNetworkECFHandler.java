package net.sinedkadis.terracompositio.ecf;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;

@MethodsReturnNonnullByDefault
public class OutOfNetworkECFHandler extends DefaultECFHandler {
    private final IEntityInstance entityInstance;

    public OutOfNetworkECFHandler(IEntityInstance entityInstance) {
        super(SentinelHelper.EMPTY_MEMBER);
        this.entityInstance = entityInstance;
    }

    @Override
    public IEntityInstance getAttachedEntity() {
        return entityInstance;
    }
}

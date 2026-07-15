package net.sinedkadis.terracompositio.api.networks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sinedkadis.terracompositio.api.IEntityInstance;

/**
 * The Common parts of Each Network Member.
 */
public interface AnyNetworkMember {
    /**
     * Gets entity. May be {@link BlockEntity},
     * or {@link Entity}
     *
     * @return the entity
     */
    IEntityInstance getEntityInstance();


    /**
     * Gets range. Using to filter interactions between network members and other purposes
     *
     * @return the range
     */
    default int getRange() {
        return getRange(false);
    }

    /**
     * Gets range. Using to filter interactions between network members and other purposes
     *
     * @param inner true if member radius requested, used in instant transfer for close positions
     * @return the range
     */
    int getRange(boolean inner);

    /**
     * Gets priority. Using to filter interactions between network members and other purposes. Default as -100 for sources, 100 for consumers
     *
     * @return the priority
     */
    int getPriority();
}

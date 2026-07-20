package net.sinedkadis.terracompositio.api;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


/**
 * The interface, implemented via mixins to {@link BlockEntity} and {@link Entity}, so ECF network can hold any
 */
@MethodsReturnNonnullByDefault
public interface IEntityInstance {
    /**
     * Cast from Entity. Needed because of mixin implementation
     *
     * @param entity the entity
     * @return the entity instance
     */
    static IEntityInstance wrap(Entity entity) {
        return ((IEntityInstance) entity);
    }

    /**
     * Cast from BlockEntity. Needed because of mixin implementation
     *
     * @param entity the entity
     * @return the entity instance
     */
    static IEntityInstance wrap(BlockEntity entity) {
        return ((IEntityInstance) entity);
    }

    /**
     * Gets block position of instance
     *
     * @return the BlockPos
     */
    BlockPos tc$getBlockPos();

    /**
     * Gets position of entity and {@link BlockPos#getCenter()} of blockEntity
     *
     * @return the vec 3
     */
    Vec3 tc$getPosition();

    /**
     * Gets level of instance
     *
     * @return the Level
     */
    Level tc$getLevel();

    /**
     *
     * @return true if {@link Entity}
     */
    boolean tc$isEntity();

    /**
     *
     * @return true if {@link BlockEntity}
     */
    default boolean tc$isBlock() {
        return !tc$isEntity();
    }

    BlockState tc$getBlockState();

    BlockEntity tc$asBE();

    Entity tc$asEntity();
}

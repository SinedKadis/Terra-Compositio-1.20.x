package net.sinedkadis.terracompositio.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@MethodsReturnNonnullByDefault
@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityInstance {

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract Level level();

    @Override
    public BlockPos tc$getBlockPos() {
        return blockPosition();
    }

    @Override
    public Vec3 tc$getPosition() {
        return position();
    }

    @Override
    public boolean tc$isEntity() {
        return true;
    }

    @Override
    public Level tc$getLevel() {
        return level();
    }

    @Override
    public BlockState tc$getBlockState() {
        throw new IllegalStateException("Tried to get blockState from entity - " + this);
    }

    @Override
    public BlockEntity tc$asBE() {
        throw new IllegalStateException("Tried to get blockEntity from entity - " + this);
    }

    @Override
    public Entity tc$asEntity() {
        return (Entity) (Object) this;
    }
}

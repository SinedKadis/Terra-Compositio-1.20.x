package net.sinedkadis.terracompositio.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@MethodsReturnNonnullByDefault
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements IEntityInstance {
    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Shadow
    public abstract BlockState getBlockState();

    @Override
    public BlockPos tc$getBlockPos() {
        return getBlockPos();
    }

    @Override
    public Vec3 tc$getPosition() {
        return getBlockPos().getCenter();
    }

    @Override
    public boolean tc$isEntity() {
        return false;
    }

}

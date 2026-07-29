package net.sinedkadis.terracompositio.api.dummies;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DummyEntityInstance implements IEntityInstance {
    @Override
    public BlockPos tc$getBlockPos() {
        return SentinelHelper.EMPTY_POS;
    }

    @Override
    public Vec3 tc$getPosition() {
        return SentinelHelper.EMPTY_POS.getCenter();
    }

    @Override
    public Level tc$getLevel() {
        throw new UnsupportedOperationException("Tried to get level from sentinel entity");
    }

    @Override
    public boolean tc$isEntity() {
        return false;
    }

    @Override
    public boolean tc$isBlock() {
        return false;
    }

    @Override
    public BlockState tc$getBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockEntity tc$asBE() {
        throw new UnsupportedOperationException("Tried to get BlockEntity from sentinel entity");
    }

    @Override
    public Entity tc$asEntity() {
        throw new UnsupportedOperationException("Tried to get Entity from sentinel entity");
    }
}

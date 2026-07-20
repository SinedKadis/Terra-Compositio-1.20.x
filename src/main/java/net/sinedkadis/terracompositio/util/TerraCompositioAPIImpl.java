package net.sinedkadis.terracompositio.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetwork;
import net.sinedkadis.terracompositio.ecf.ECFNetworkHandler;
import net.sinedkadis.terracompositio.fluid.FluidNetworkHandler;
import net.sinedkadis.terracompositio.registries.TCSounds;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;

public class TerraCompositioAPIImpl implements TerraCompositioAPI {
    @Override
    public ECFNetwork getECFNetworkInstance() {
        return ECFNetworkHandler.INSTANCE;
    }

    @Override
    public FluidNetwork getFluidNetworkInstance() {
        return FluidNetworkHandler.INSTANCE;
    }

    @Override
    public void playFlowEvaporationSound(Level level, BlockPos pos) {
        level.playSound(null, pos, TCSounds.FLOW_EVAPORATION.get(), SoundSource.BLOCKS);
    }

    @Override
    public void spawnECFParticles(Level pLevel, BlockPos targetPos, int count) {
        ParticleHelperInternal.spawnParticlesIn(pLevel, targetPos, count);
    }
}

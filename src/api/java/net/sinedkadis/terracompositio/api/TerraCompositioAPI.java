package net.sinedkadis.terracompositio.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetwork;


/**
 * The TerraCompositio API. Implemented like Botania API,
 * except I don't understand how to work with that, so sorry if it not works. Write your issues is GitHub repo
 */
public interface TerraCompositioAPI {
    /**
     * The constant TerraCompositio MOD_ID.
     */
    String MOD_ID = "terracompositio";

    /**
     * The constant TerraCompositioAPI Instance.
     */
    TerraCompositioAPI INSTANCE = ServiceUtil.findService(TerraCompositioAPI.class, () -> new TerraCompositioAPI() {});

    /**
     * TerraCompositioAPI Instance getter.
     *
     * @return the TerraCompositioAPI
     */
    static TerraCompositioAPI instance(){
        return INSTANCE;
    }

    /**
     * Get ECF Network Instance if mod is loaded, overwise placeholder.
     *
     * @return the instance
     */
    default ECFNetwork getECFNetworkInstance(){
        return SentinelHelper.EMPTY_NETWORK;
    }

    /**
     * Get Fluid Network Instance if mod is loaded, overwise placeholder.
     *
     * @return the fluid network
     */
    default FluidNetwork getFluidNetworkInstance(){
        return SentinelHelper.EMPTY_NETWORK;
    }

    /**
     * Play flow evaporation sound. Call on server
     *
     * @param level the level
     * @param pos   the pos
     */
    default void playFlowEvaporationSound(Level level, BlockPos pos) {

    }

    /**
     * Spawn ecf particles. Call on server
     *
     * @param pLevel    the p level
     * @param targetPos the target pos
     * @param count     the particle count
     */
    default void spawnECFParticles(Level pLevel, BlockPos targetPos, int count) {

    }
}

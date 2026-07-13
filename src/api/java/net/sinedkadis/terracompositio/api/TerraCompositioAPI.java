package net.sinedkadis.terracompositio.api;

import net.sinedkadis.terracompositio.api.dummies.DummyNetwork;
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
        return DummyNetwork.instance;
    }

    /**
     * Get Fluid Network Instance if mod is loaded, overwise placeholder.
     *
     * @return the fluid network
     */
    default FluidNetwork getFluidNetworkInstance(){
        return DummyNetwork.instance;
    }
}

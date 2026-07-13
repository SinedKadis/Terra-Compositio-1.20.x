package net.sinedkadis.terracompositio.api.registries;


import net.minecraft.world.level.GameRules;
import net.sinedkadis.terracompositio.api.helpers.GameruleHelper;


public class TCGameRules {
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_FLOW_LEAKING;
    public static void init() {
        DISABLE_FLOW_LEAKING = GameruleHelper.register("disableFlowLeaking", GameRules.Category.MISC, false);
    }
}

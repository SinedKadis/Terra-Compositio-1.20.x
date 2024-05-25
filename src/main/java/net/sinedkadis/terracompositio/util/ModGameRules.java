package net.sinedkadis.terracompositio.util;


import net.minecraft.world.level.GameRules;


public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_FLOW_LEAKING;
    public static void init() {
        DISABLE_FLOW_LEAKING = GameruleUtilities.register("disableFlowLeaking", GameRules.Category.MISC,false);
    }
}

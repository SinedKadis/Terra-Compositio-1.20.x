package net.sinedkadis.terracompositio.fluid;

import net.minecraftforge.event.TickEvent;

public class SwimEventHandler {

    public static void register() {
        TickEvent.PLAYER_PRE.register(SwimEventHandler::onPlayerTick);
    }

    private static void onPlayerTick(Player player) {
        SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
        if (swimData != null) {
            if (player.isInWater() || player.isInLava()) {
                if (!swimData.isWet()) {
                    swimData.setWet(true);
                }
            } else if (player.onGround() || player.getAbilities().flying) {
                swimData.setWet(false);
            }
        }
    }

    public static EventResult onPlayerSwim(Player player) {
        SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
        if (swimData != null) {
            if (swimData.isSwimming()) {
                return EventResult.interruptTrue();
            } else if (CharmOfSinkingItem.shouldSink(player)) {
                return EventResult.interruptFalse();
            }
        }
        return EventResult.pass();
    }
}
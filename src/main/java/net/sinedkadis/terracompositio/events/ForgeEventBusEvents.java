package net.sinedkadis.terracompositio.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.effect.ModEffects;
import net.sinedkadis.terracompositio.particle.ModParticles;

@Mod.EventBusSubscriber(modid = TerraCompositio.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusEvents {
    private static int counter;
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (player != null && !(player instanceof FakePlayer)) {
                    Vec3 pos = player.getEyePosition();
                    if (player.hasEffect(ModEffects.FLOW_SATURATION.get()) && counter++ > 5) {
                        counter = 0;
                        serverLevel.sendParticles(player, ModParticles.FLOW_PARTICLE.get(),
                                true, pos.x, pos.y, pos.z, 10, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        }
    }
}
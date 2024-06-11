package net.sinedkadis.terracompositio.events;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.custom.WedgeBlock;
//import net.sinedkadis.terracompositio.network.PacketHandler;
import net.sinedkadis.terracompositio.particle.ModParticles;
import net.sinedkadis.terracompositio.particle.custom.BirchJuiceParticle;
import net.sinedkadis.terracompositio.particle.custom.BirchJuiceSplashParticle;
import net.sinedkadis.terracompositio.particle.custom.FlowParticle;
import net.sinedkadis.terracompositio.particle.custom.FlowSplashParticle;

@Mod.EventBusSubscriber(modid = TerraCompositio.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    /*@SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.RHINO_LAYER, RhinoModel::createBodyLayer);
    }*/

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.FLOW_PARTICLE.get(),
                FlowParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BIRCH_JUICE_PARTICLE.get(),
                BirchJuiceParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.FLOW_SPLASH_PARTICLE.get(),
                FlowSplashParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BIRCH_JUICE_SPLASH_PARTICLE.get(),
                BirchJuiceSplashParticle.Provider::new);
    }


}

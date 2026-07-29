package net.sinedkadis.terracompositio.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.item.custom.TechnetiumArmorItem;
import net.sinedkadis.terracompositio.registries.TCFluids;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class ForgeEventBusEvents {
    @SubscribeEvent
    public static void onLivingHurt(EntityInvulnerabilityCheckEvent event) {
        TechnetiumArmorItem.onLivingHurtEvent(event);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        TCFluids.applyLiquidFlowEffect(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        FlowCedarEntEntity.onEntityInteractEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        ECFNetwork ecfNetworkInstance = TerraCompositioAPI.INSTANCE.getECFNetworkInstance();
        Level level = player.level();
        ECFNetworkMember member = ((ECFNetworkMember) player);
        if (!level.isClientSide) {

            boolean inNetwork = ecfNetworkInstance.isIn(level, member);
            if (!inNetwork && !player.isRemoved()) {
                ecfNetworkInstance.fireECFNetworkEvent(member, NetworkAction.ADD);
                member.scheduleMemberUpdate();
            }
        }
        member.updateIfScheduled();
    }


}
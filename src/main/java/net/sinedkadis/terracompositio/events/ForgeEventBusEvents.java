package net.sinedkadis.terracompositio.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.ecf.PlayerECFProvider;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.item.custom.KnowledgeAppleItem;
import net.sinedkadis.terracompositio.item.custom.TechnetiumArmorItem;
import net.sinedkadis.terracompositio.registries.TCFluids;

@Mod.EventBusSubscriber(modid = TerraCompositio.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingAttackEvent event) {
        TechnetiumArmorItem.onLivingHurtEvent(event);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        TCFluids.applyLiquidFlowEffect(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        FlowCedarEntEntity.onEntityInteractEvent(event);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(TCCapabilities.ECF).isPresent()) {
                event.addCapability(TerraCompositio.modLoc("ecf_stored"), new PlayerECFProvider(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        KnowledgeAppleItem.onPlayerClonedEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
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
package net.sinedkadis.terracompositio.ecf;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlayerECFProvider implements ICapabilityProvider<Player, Void, IECFHandler> {

    private final Map<Player, ECFHandlerPlayerArmor> providers = new HashMap<>();

    public PlayerECFProvider() {
    }


    @Override
    public @Nullable IECFHandler getCapability(Player player, Void context) {
        return providers.computeIfAbsent(player, p -> new ECFHandlerPlayerArmor(new DefaultECFHandler((ECFNetworkMember) player)
                .setMaxECF(0) // I haven't thought of a use for this yet
                .setOffset(vec3 -> vec3.add(0, 1, 0))));
    }
}

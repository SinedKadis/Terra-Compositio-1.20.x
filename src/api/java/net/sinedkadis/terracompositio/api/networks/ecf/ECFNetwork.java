package net.sinedkadis.terracompositio.api.networks.ecf;

import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.AnyNetworkMember;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;

import java.util.Set;
import java.util.function.IntBinaryOperator;

/**
 * The ECF network singleton. Used to manage interactions between different {@link ECFNetworkMember}.
 */
public interface ECFNetwork {
    /**
     * Fire forge ecf network event.
     *
     * @param source the source
     * @param action the action
     */
    void fireECFNetworkEvent(ECFNetworkMember source, NetworkAction action);

    /**
     * Checks if member already added to network.
     *
     * @param pLevel     the p level
     * @param ecfHandler the ecf handler
     * @return the boolean
     */
    boolean isIn(Level pLevel, ECFNetworkMember ecfHandler);

    /**
     * Checks for validating given member.
     *
     * @param target the member
     * @return the true if member is valid
     */
    boolean validateMember(AnyNetworkMember target);

    /**
     * Checks for validating relation between given members.
     *
     * @param source the source member
     * @param target the target member
     * @param distanceOp the operation, that returns range between two members, combined from their range property
     * @return the true if member is valid
     */
    boolean validateRelation(ECFNetworkMember source, ECFNetworkMember target, IntBinaryOperator distanceOp);

    void executeECFTransfer(ECFNetworkMember target,
                            ECFNetworkMember source,
                            float speed);

    void sendBurst(IECFHandler source, ECFNetworkMember target, int count, float speed);

    /**
     * Searches for available to sent ECF members
     *
     * @param requesterMember the member that request search
     * @return the available network targets
     */
    Set<ECFNetworkMember> getAvailableNetworkTargets(ECFNetworkMember requesterMember);

    /**
     * Gets all ecf network members in given world.
     *
     * @param level the level
     * @return the all ecf network members
     */
    Set<ECFNetworkMember> getAllECFNetworkMembers(Level level);

    /**
     * Creates default ecf handler if Terracompositio exist. Usable in
     * {@link net.minecraft.world.level.block.entity.BlockEntity},
     * {@link net.minecraft.world.entity.Entity} and other
     *
     * @param entityInstance the attached to handler member
     * @return the iecf handler
     */
    default IECFHandler createDefaultECFHandler(ECFNetworkMember entityInstance) {
        return SentinelHelper.EMPTY_ECF_HANDLER;
    }
}

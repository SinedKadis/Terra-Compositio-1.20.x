package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.ecf.OutOfNetworkECFHandler;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderCoreBlockEntity extends TCBlockEntity {
    public FEProviderCoreBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    void addBEBehaviours(List<IBEBehaviour> behaviourList) {
        behaviourList.add(new ECFHandlerBehaviour(this)
                .ecfHandler(ecfHandlerBehaviour -> new OutOfNetworkECFHandler(ecfHandlerBehaviour.getEntityInstance()))
                .ecfNetworkMember(ecfHandlerBehaviour -> SentinelHelper.EMPTY_MEMBER)
        );
    }
}

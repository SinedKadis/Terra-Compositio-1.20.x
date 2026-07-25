package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.ecf.OutOfNetworkECFHandler;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderCoreBlockEntity extends TCBlockEntity {

    private final IEnergyStorage energyStorage = new EnergyStorage(3200);

    public FEProviderCoreBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        IECFHandler ecfCapability = getECFCapability(null);
        if (ecfCapability.getECF() > 0) {
            int toAddEnergy = energyStorage.receiveEnergy(ecfCapability.takeECF(1, TransferAction.SIMULATE) * 20, true);
            if (toAddEnergy > 0) {
                ecfCapability.takeECF(1, TransferAction.EXECUTE);
                energyStorage.receiveEnergy(toAddEnergy, false);
                ParticleHelperInternal.spawnParticlesIn(pLevel, pPos);
            }
        }
    }

    @Override
    void addBEBehaviours(List<IBEBehaviour> behaviourList) {
        behaviourList.add(new ECFHandlerBehaviour(this)
                .ecfHandler(ecfHandlerBehaviour -> new OutOfNetworkECFHandler(ecfHandlerBehaviour.getEntityInstance()))
                .ecfNetworkMember(ecfHandlerBehaviour -> SentinelHelper.EMPTY_MEMBER)
        );
    }

    @Override
    public IEnergyStorage getEnergyCapability(@Nullable Direction direction) {
        if (direction == null ||
                direction.equals(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)))
            return energyStorage;
        return SentinelHelper.EMPTY_ENERGY_HANDLER;
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        super.collectKnowledgeData(data, provider);
        data.putInt(TooltipHelper.Keys.FE.toData(), energyStorage.getEnergyStored());
        data.putInt(TooltipHelper.Keys.MAX_FE.toData(), energyStorage.getMaxEnergyStored());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        super.addTooltipLines(data, tooltip, isShifting, provider);
        TooltipHelper.addWithHeader(TooltipHelper.Headers.FE, tooltip, t -> {
            if (!isShifting) {
                TooltipHelper.addScaleIfExist(TooltipHelper.Keys.FE, TooltipHelper.Keys.MAX_FE, t, data, ChatFormatting.DARK_RED);
            } else {
                TooltipHelper.addIfExist(TooltipHelper.Keys.FE, t, data);
                if (TCCommonConfigs.DEBUG.get()) {
                    TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_FE, t, data);
                }
            }
        });
    }
}

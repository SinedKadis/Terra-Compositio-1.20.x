package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.ecf.OutOfNetworkECFHandler;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderCoreBlockEntity extends TCBlockEntity {

    private final IEnergyStorage energyStorage = new EnergyStorage(3200);

    private LazyOptional<IEnergyStorage> lazyOptional = LazyOptional.empty();

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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOptional.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyOptional = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void collectKnowledgeData(CompoundTag data) {
        super.collectKnowledgeData(data);
        data.putInt(TooltipHelper.Keys.FE.toData(), energyStorage.getEnergyStored());
        data.putInt(TooltipHelper.Keys.MAX_FE.toData(), energyStorage.getMaxEnergyStored());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        super.addTooltipLines(data, tooltip, isShifting);
        TooltipHelper.addWithHeader(TooltipHelper.Headers.FE, tooltip, t -> {
            if (!isShifting) {
                TooltipHelper.addScaleIfExist(TooltipHelper.Keys.FE, TooltipHelper.Keys.MAX_FE, t, data);
            } else {
                TooltipHelper.addIfExist(TooltipHelper.Keys.FE, t, data);
                if (TCCommonConfigs.DEBUG.get()) {
                    TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_FE, t, data);
                }
            }
        });
    }
}

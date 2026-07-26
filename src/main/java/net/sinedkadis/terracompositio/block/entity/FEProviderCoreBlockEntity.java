package net.sinedkadis.terracompositio.block.entity;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.sinedkadis.terracompositio.api.helpers.BlockPosHelper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FEProviderCoreBlockEntity extends TCBlockEntity {

    private final IEnergyStorage energyStorage = new EnergyStorage(3200);
    @Getter
    private final Set<BlockPos> pylonPoses = new HashSet<>();

    private LazyOptional<IEnergyStorage> lazyOptional = LazyOptional.empty();

    public FEProviderCoreBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        IECFHandler ecfCapability = getCapability(TCCapabilities.ECF)
                .orElse(SentinelHelper.EMPTY_ECF_HANDLER);
        if (ecfCapability.getECF() > 0 && pLevel.getGameTime() % 20 == 3) {
            for (int i = 0; i < pylonPoses.size(); i++) {
                int toAddEnergy = energyStorage.receiveEnergy(ecfCapability.takeECF(1, TransferAction.SIMULATE) * 20, true);
                if (toAddEnergy > 0) {
                    ecfCapability.takeECF(1, TransferAction.EXECUTE);
                    energyStorage.receiveEnergy(toAddEnergy, false);
                    ParticleHelperInternal.spawnParticlesIn(pLevel, pPos);
                }
            }
        }
    }

    @Override
    void addBEBehaviours(List<IBEBehaviour> behaviourList) {
        behaviourList.add(new ECFHandlerBehaviour(this)
                .maxECF(128)
                .priority(TCInnerConfig.DEFAULT_CONSUMER_PRIORITY)
                .range(7)
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        BlockPosHelper.saveFromSetToTag(tag, "pylon_poses", pylonPoses);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        BlockPosHelper.loadFromTagToSet(tag, "pylon_poses", pylonPoses);
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

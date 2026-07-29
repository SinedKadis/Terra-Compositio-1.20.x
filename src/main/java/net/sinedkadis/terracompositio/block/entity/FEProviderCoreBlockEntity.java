package net.sinedkadis.terracompositio.block.entity;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.sinedkadis.terracompositio.api.helpers.BlockPosHelper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
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

    public ITCRecipe.CraftException exception = ITCRecipe.CraftException.OK;
    public boolean exceptionLock = false;


    public FEProviderCoreBlockEntity(BlockPos pos, BlockState pState) {
        super(pos, pState);
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);

        transferEnergy(pLevel, pPos, pState);

        if (exceptionLock) return;

        IECFHandler ecfCapability = getECFCapability(null);
        if (!pState.getValue(TCBlockStateProperties.INFUSED)) {
            exception = ITCRecipe.CraftException.NO_SURROUNDINGS;
            return;
        }
        if (ecfCapability.getECF() <= 0) {
            exception = ITCRecipe.CraftException.NO_ECF;
            return;
        }
        if (energyStorage.receiveEnergy(ecfCapability.takeECF(1, TransferAction.SIMULATE) * 20, true) <= 0) {
            exception = ITCRecipe.CraftException.NO_SPACE;
            return;
        }
        if (pLevel.getGameTime() % 20 == 3) {
            for (int i = 0; i < pylonPoses.size(); i++) {
                int toAddEnergy = energyStorage.receiveEnergy(ecfCapability.takeECF(1, TransferAction.SIMULATE) * 20, true);
                if (toAddEnergy > 0) {
                    ecfCapability.takeECF(1, TransferAction.EXECUTE);
                    energyStorage.receiveEnergy(toAddEnergy, false);
//                    ParticleHelperInternal.spawnParticlesIn(pLevel, pPos);
                }
            }
        }
        exception = ITCRecipe.CraftException.OK;
    }

    public void transferEnergy(Level level, BlockPos pPos, BlockState pState) {
        if (energyStorage.getEnergyStored() > 0) {
            Direction direction = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pPos.relative(direction), direction);
            if (capability != null) {
                int toTransfer = capability.receiveEnergy(energyStorage.extractEnergy(energyStorage.getEnergyStored(), true), true);
                if (toTransfer > 0) {
                    energyStorage.extractEnergy(toTransfer, false);
                    capability.receiveEnergy(toTransfer, false);
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        BlockPosHelper.saveFromSetToTag(tag, "pylon_poses", pylonPoses);
        tag.put("energy", ((EnergyStorage) energyStorage).serializeNBT(registries));
        tag.putString("exception", exception.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        BlockPosHelper.loadFromTagToSet(tag, "pylon_poses", pylonPoses);
        Tag energy = tag.get("energy");
        if (energy != null)
            ((EnergyStorage) energyStorage).deserializeNBT(registries, energy);
        exception = Enum.valueOf(ITCRecipe.CraftException.class, tag.getString("exception"));
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
        if (!exception.equals(ITCRecipe.CraftException.OK)) {
            data.putString(TooltipHelper.Keys.CRAFT_EXCEPTION.toData(), exception.name());
        }
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
        TooltipHelper.addWithHeader(TooltipHelper.Headers.CRAFTING, tooltip, t -> {

            if (data.contains(TooltipHelper.Keys.CRAFT_EXCEPTION.toData())) {
                TooltipHelper.addWithNoArg(
                        TooltipHelper.Keys.CRAFT_EXCEPTION,
                        Enum.valueOf(ITCRecipe.CraftException.class, data.getString(TooltipHelper.Keys.CRAFT_EXCEPTION.toData())),
                        t
                );
            }
        });
    }
}

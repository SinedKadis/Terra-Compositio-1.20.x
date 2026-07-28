package net.sinedkadis.terracompositio.block.entity;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
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

    public ITCRecipe.CraftException exception = ITCRecipe.CraftException.OK;
    public boolean exceptionLock = false;


    public FEProviderCoreBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);

        transferEnergy(pLevel, pPos, pState);

        if (exceptionLock) return;

        IECFHandler ecfCapability = getCapability(TCCapabilities.ECF)
                .orElse(SentinelHelper.EMPTY_ECF_HANDLER);
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
                    ParticleHelperInternal.spawnParticlesIn(pLevel, pPos);
                }
            }
        }
        exception = ITCRecipe.CraftException.OK;
    }

    public void transferEnergy(Level level, BlockPos pPos, BlockState pState) {
        if (energyStorage.getEnergyStored() > 0) {
            Direction direction = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockEntity blockEntity = level.getBlockEntity(pPos.relative(direction));
            if (blockEntity != null) {
                IEnergyStorage capability = blockEntity.getCapability(ForgeCapabilities.ENERGY)
                        .orElse(SentinelHelper.EMPTY_ENERGY_HANDLER);
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        BlockPosHelper.saveFromSetToTag(tag, "pylon_poses", pylonPoses);
        tag.put("energy", ((EnergyStorage) energyStorage).serializeNBT());
        tag.putString("exception", exception.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        BlockPosHelper.loadFromTagToSet(tag, "pylon_poses", pylonPoses);
        Tag energy = tag.get("energy");
        if (energy != null)
            ((EnergyStorage) energyStorage).deserializeNBT(energy);
        exception = Enum.valueOf(ITCRecipe.CraftException.class, tag.getString("exception"));

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
        if (!exception.equals(ITCRecipe.CraftException.OK)) {
            data.putString(TooltipHelper.Keys.CRAFT_EXCEPTION.toData(), exception.name());
        }
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

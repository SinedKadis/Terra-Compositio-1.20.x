package net.sinedkadis.terracompositio.block.entity;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;


@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TimePassageDesorberBlockEntity extends AbstractDesorberBlockEntity {
    private int timeBuffer = 0;
    private int timeReSetter = 20;
    private int timeCounter = 0;
    public static final Function<Integer,Double> function = (x) -> (-Math.cos((double) x /50)+1)/1.5f;
    public TimePassageDesorberBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.TIME_PASSAGE_DESORBER_BE.get(), pos, state);
    }

    @Override
    protected int getTankCapacity() {
        return 1000;
    }

    @Override
    protected int getMaxCFE() {
        return 240;
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        timeReSetter--;
        if (timeReSetter <= 1) {
            timeReSetter = 20;
            if (pLevel.hasNeighborSignal(pPos)){
                addingECFProcess();
            }else if (isEnoughFluid()) {
                addingTimeProcess(pLevel);
            }
        }
    }

    private void addingTimeProcess(Level pLevel) {
        timeCounter++;
        consumeFluid();
        Double chance = function.apply(timeCounter);
        float random = pLevel.getRandom().nextFloat();
        if (random < chance) {
            timeBuffer++;
            if (chance > 1 && random < (chance-1)){
                timeBuffer++;
                ParticleHelperInternal.spawnParticlesIn(pLevel, this.worldPosition);
            }
        }
    }

    private void addingECFProcess() {
        timeCounter = 0;
        if (timeBuffer > 20) {
            timeBuffer -= ecfContainer().addECF(20, TransferAction.BOTH);
        } else if (timeBuffer > 0){
            timeBuffer -= ecfContainer().addECF(timeBuffer, TransferAction.BOTH);
        }
    }


    private void consumeFluid() {
        this.fluidHandler.drain(10, IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean isEnoughFluid() {
        return !this.fluidHandler.isEmpty() && this.fluidHandler.getFluidAmount() >= 10;
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("buffer", timeBuffer);
        tag.putInt("counter", timeCounter);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        timeBuffer = tag.getInt("buffer");
        timeCounter = tag.getInt("counter");
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        data.putInt(TooltipHelper.Keys.TIME_COLLECTED.toData(), timeBuffer);
        data.putDouble(TooltipHelper.Keys.TIME_COLLECTION_CHANCE.toData(), function.apply(timeCounter) * 100);
        super.collectKnowledgeData(data, provider);
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.BLOCK, tooltip, t -> {
            TooltipHelper.addIfExist(TooltipHelper.Keys.TIME_COLLECTED, TooltipHelper.Units.SECONDS, t, data);
            TooltipHelper.addIfExist(TooltipHelper.Keys.TIME_COLLECTION_CHANCE, TooltipHelper.Units.NO_UNITS, t, data);
        });

        super.addTooltipLines(data, tooltip, isShifting, provider);
    }
}

package net.sinedkadis.terracompositio.block.entity;


import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sinedkadis.terracompositio.api.components.FluidComponent;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractDesorberBlockEntity extends TCBlockEntity {

    protected final FluidTank fluidHandler = new FluidTank(getTankCapacity()){
        private final FluidStack flow = new FluidStack(TCFluids.FLOW_FLUID.source.get(), 1000);
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (FluidStack.isSameFluidSameComponents(resource, flow))
                return super.fill(resource, action);
            return 0;
        }
    };

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {
        list.add(new ECFHandlerBehaviour(this)
                .maxECF(getMaxCFE())
                .range(5)
                .priority(TCInnerConfig.DEFAULT_SOURCE_PRIORITY));
    }

    abstract protected int getMaxCFE();

    //todo comparator compatibility


    protected int getTankCapacity() {
        return 250;
    }

    public AbstractDesorberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel,pPos,pState);
        if (!fluidHandler.isEmpty() && !pState.getValue(TCBlockStateProperties.INFUSED)) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(TCBlockStateProperties.INFUSED, true));
        } else if (fluidHandler.isEmpty() && pState.getValue(TCBlockStateProperties.INFUSED)) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(TCBlockStateProperties.INFUSED, false));
        }

        IFluidHandler fluidHandlerOptional = pLevel.getCapability(Capabilities.FluidHandler.BLOCK, pPos.below(), null);
        if (fluidHandlerOptional instanceof FluidTank sourceTank) {
            FluidUtil.tryFluidTransfer(this.fluidHandler,sourceTank,1000,true);
        }
    }



    protected IECFHandler ecfContainer() {
        return ((ECFHandlerBehaviour) behaviours.getFirst()).getMainHandler();
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        super.collectKnowledgeData(data, provider);

        FluidStack fluidInTank = fluidHandler.getFluidInTank(0);
        CompoundTag compoundTag = (CompoundTag) fluidInTank.saveOptional(provider);
        data.put(TooltipHelper.Keys.FLUID.toData(), compoundTag);

    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        super.addTooltipLines(data, tooltip, isShifting, provider);

        FluidStack fluidStack = FluidStack.parseOptional(provider, data.getCompound(TooltipHelper.Keys.FLUID.toData()));
        if (!fluidStack.isEmpty()) {
            TooltipHelper.addWithHeader(TooltipHelper.Headers.FLUIDS, tooltip,
                    t -> t.add(FluidComponent.of(fluidStack)));
        }

    }

    @Override
    public IFluidHandler getFluidCapability(@Nullable Direction direction) {
        return fluidHandler;
    }
}

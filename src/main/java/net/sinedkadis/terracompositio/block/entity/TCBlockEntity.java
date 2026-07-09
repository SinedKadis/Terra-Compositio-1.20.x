package net.sinedkadis.terracompositio.block.entity;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.custom.TCBaseEntityBlock;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderInstance;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEECFBehaviour;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEItemBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TCBlockEntity extends BlockEntity implements IHaveKnowledge, ITCCapabilityProviderInstance {
    @Getter
    protected List<IBEBehaviour> behaviours = new ArrayList<>();

    public TCBlockEntity(BlockPos pos, BlockState state) {
        super(((TCBaseEntityBlock) state.getBlock()).getBlockEntityType(), pos, state);
        addBEBehaviours(behaviours);
    }
    public TCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        addBEBehaviours(behaviours);
    }

    abstract void addBEBehaviours(List<IBEBehaviour> behaviourList);

    public Set<IBEItemBehaviour> getItemBehaviours() {
        Set<IBEItemBehaviour> toReturn = new HashSet<>();
        for (IBEBehaviour ibeBehaviour : behaviours) {
            if (ibeBehaviour instanceof IBEItemBehaviour ibeItemBehaviour) toReturn.add(ibeItemBehaviour);
        }
        return toReturn;
    }

    public @Nullable IBEECFBehaviour getECFBehaviour() {
        for (IBEBehaviour ibeBehaviour : behaviours) {
            if (ibeBehaviour instanceof IBEECFBehaviour IBEECFBehaviour) return IBEECFBehaviour;
        }
        return null;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (level instanceof ServerLevel)
            behaviours.forEach(IBEBehaviour::tick);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        behaviours.forEach(IBEBehaviour::onChunkLoad);
    }

    @Override
    public void setRemoved() {
        behaviours.forEach(IBEBehaviour::onRemoved);
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        behaviours.forEach(iBehaviour -> iBehaviour.onSave(tag, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        behaviours.forEach(iBehaviour -> iBehaviour.onLoad(tag, registries));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        for (IBEBehaviour behaviour : getBehaviours()) {
            if (behaviour instanceof IHaveKnowledge iHaveKnowledge) {
                iHaveKnowledge.addTooltipLines(data, tooltip, isShifting, provider);
            }
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        for (IBEBehaviour behaviour : getBehaviours()) {
            if (behaviour instanceof IHaveKnowledge iHaveKnowledge) {
                iHaveKnowledge.collectKnowledgeData(data, provider);
            }
        }
    }

    @Override
    public IItemHandler getItemCapability(@Nullable Direction direction) {
        Optional<IItemHandler> behaviourCap = behaviours.stream()
                .map(iBehaviour -> iBehaviour.getItemCapability(direction))
                .filter(Objects::nonNull)
                .findAny();
        return behaviourCap.orElse(EmptyItemHandler.INSTANCE);
    }

    @Override
    public IECFHandler getECFCapability(@Nullable Direction direction) {
        Optional<IECFHandler> behaviourCap = behaviours.stream()
                .map(iBehaviour -> iBehaviour.getECFCapability(direction))
                .filter(Objects::nonNull)
                .findAny();
        return behaviourCap.orElse(SentinelHelper.EMPTY_ECF_HANDLER);
    }

    @Override
    public IFluidHandler getFluidCapability(@Nullable Direction direction) {
        Optional<IFluidHandler> behaviourCap = behaviours.stream()
                .map(iBehaviour -> iBehaviour.getFluidCapability(direction))
                .filter(Objects::nonNull)
                .findAny();
        return behaviourCap.orElse(EmptyFluidHandler.INSTANCE);
    }

    @Override
    public IItemHandler getStateHolderCapability(@Nullable Direction direction) {
        Optional<IItemHandler> behaviourCap = behaviours.stream()
                .map(iBehaviour -> iBehaviour.getStateHolderCapability(direction))
                .filter(Objects::nonNull)
                .findAny();
        return behaviourCap.orElse(EmptyItemHandler.INSTANCE);
    }
}
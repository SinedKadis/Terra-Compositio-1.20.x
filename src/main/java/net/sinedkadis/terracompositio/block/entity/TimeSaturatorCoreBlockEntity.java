package net.sinedkadis.terracompositio.block.entity;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.AnyNetworkMember;
import net.sinedkadis.terracompositio.block.behaviours.AssemblyBehaviour;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import net.sinedkadis.terracompositio.util.helpers.WorldHelperInternal;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TimeSaturatorCoreBlockEntity extends TCBlockEntity implements IHaveKnowledge, AnyNetworkMember {

    private static final int EXTRA_RANGE_PER_BLOCK = 4;
    @Getter

    private int range;

    public TimeSaturatorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    void addBEBehaviours(List<IBEBehaviour> behaviourList) {
        behaviourList.add(new AssemblyBehaviour(this) {
            @Override
            protected boolean isAssembled(ServerLevel level) {
                return ((TimeSaturatorCoreBlockEntity) blockEntity).isAssembled(level);
            }
        });
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        CompoundTag persistentData = this.getPersistentData();
        boolean isAssembled = !persistentData.contains(AssemblyBehaviour.ASSEMBLY.toData()) || persistentData.getBoolean(AssemblyBehaviour.ASSEMBLY.toData());
        if (isAssembled) {
            List<TickingBlockEntity> list = List.copyOf(pLevel.blockEntityTickers).stream()
                    .filter(tickingBlockEntity -> tickingBlockEntity.getPos().closerThan(pPos, getRange()))
                    .filter(tickingBlockEntity -> !pLevel.getBlockState(tickingBlockEntity.getPos())
                            .is(TCBlocks.TIME_SATURATOR_CORE.get()))
                    .toList();
            for (int i = 0; i < 3; i++) {
                list.stream()
                        .filter(tickingBlockEntity -> !tickingBlockEntity.isRemoved())
                        .forEach(TickingBlockEntity::tick);
            }
        }
    }


    public boolean isAssembled(ServerLevel level) {
        BlockPos blockPos = this.getBlockPos();
        int range = 0;

        outer:
        for (int i = 1; i < 8; i++) {
            BlockPos below = blockPos.below(i);
            BlockState state = level.getBlockState(below);

            if (!state.is(TCBlocks.TECHNETIUM_BLOCK)) {
                int oldRange = this.range;
                this.range = 0;
                if (oldRange != 0)
                    level.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), 3);
                return false;
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos relative = below.relative(direction);
                BlockState desorber = level.getBlockState(relative);
                if (desorber.is(TCBlocks.TIME_PASSAGE_DESORBER)) {
                    if (!(WorldHelperInternal.containsFlow(level, relative))
                            || !(WorldHelperInternal.containsFlow(level, relative.below()))) {
                        ++range;
                        continue outer;
                    }
                } else {
                    ++range;
                    continue outer;
                }
            }
            if (level.getBlockState(below.below()).is(TCBlocks.TECHNETIUM_BLOCK)) {
                int newRange = (range + 1) * EXTRA_RANGE_PER_BLOCK;
                int oldRange = this.range;
                this.range = newRange;
                if (oldRange != newRange)
                    level.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), 3);
                return true;
            }
        }
        int oldRange = this.range;
        this.range = 0;
        if (oldRange != 0)
            level.sendBlockUpdated(blockPos, getBlockState(), getBlockState(), 3);

        return false;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag updateTag = super.getUpdateTag(registries);
        updateTag.putInt(TooltipHelper.Keys.RANGE.toData(), range);
        return updateTag;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        range = tag.getInt(TooltipHelper.Keys.RANGE.toData());
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        super.collectKnowledgeData(data, provider);
        data.putInt(TooltipHelper.Keys.RANGE.toData(), this.getRange());
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        super.addTooltipLines(data, tooltip, isShifting, provider);
        TooltipHelper.addWithHeader(TooltipHelper.Headers.BLOCK, tooltip, t ->
                TooltipHelper.addIfExist(TooltipHelper.Keys.RANGE, TooltipHelper.Units.BLOCKS, t, data));
    }

    @Override
    public IEntityInstance getEntityInstance() {
        return IEntityInstance.wrap(this);
    }

    @Override
    public int getRange(boolean inner) {
        return inner ? 1 : getRange();
    }

    @Override
    public int getPriority() {
        return 0;
    }
}

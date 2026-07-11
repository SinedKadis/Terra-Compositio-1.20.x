package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import net.sinedkadis.terracompositio.block.entity.FloatingComparatorBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FloatingComparatorBlock extends ComparatorBlock {
    public FloatingComparatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurviveOn(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FloatingComparatorBlockEntity(pos, state);
    }

    @Override
    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity instanceof FloatingComparatorBlockEntity ? ((FloatingComparatorBlockEntity) blockentity).getOutputSignal() : 0;
    }

    @Override
    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        if (!level.getBlockTicks().willTickThisTick(pos, this)) {
            int i = this.calculateOutputSignal(level, pos, state);
            BlockEntity blockentity = level.getBlockEntity(pos);
            int j = blockentity instanceof FloatingComparatorBlockEntity ? ((FloatingComparatorBlockEntity) blockentity).getOutputSignal() : 0;
            if (i != j || state.getValue(POWERED) != this.shouldTurnOn(level, pos, state)) {
                TickPriority tickpriority = this.shouldPrioritize(level, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
                level.scheduleTick(pos, this, 2, tickpriority);
            }
        }
    }

    private int calculateOutputSignal(Level level, BlockPos pos, BlockState state) {
        int i = this.getInputSignal(level, pos, state);
        if (i == 0) {
            return 0;
        } else {
            int j = this.getAlternateSignal(level, pos, state);
            if (j > i) {
                return 0;
            } else {
                return state.getValue(MODE) == ComparatorMode.SUBTRACT ? i - j : i;
            }
        }
    }

    private void refreshOutputState(Level level, BlockPos pos, BlockState state) {
        int i = this.calculateOutputSignal(level, pos, state);
        BlockEntity blockentity = level.getBlockEntity(pos);
        int j = 0;
        if (blockentity instanceof FloatingComparatorBlockEntity comparatorblockentity) {
            j = comparatorblockentity.getOutputSignal();
            comparatorblockentity.setOutputSignal(i);
        }

        if (j != i || state.getValue(MODE) == ComparatorMode.COMPARE) {
            boolean flag1 = this.shouldTurnOn(level, pos, state);
            boolean flag = state.getValue(POWERED);
            if (flag && !flag1) {
                level.setBlock(pos, state.setValue(POWERED, Boolean.FALSE), 2);
            } else if (!flag && flag1) {
                level.setBlock(pos, state.setValue(POWERED, Boolean.TRUE), 2);
            }

            this.updateNeighborsInFront(level, pos, state);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.refreshOutputState(level, pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        } else {
            state = state.cycle(MODE);
            float f = state.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
            level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, f);
            level.setBlock(pos, state, 2);
            this.refreshOutputState(level, pos, state);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }
}

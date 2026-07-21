package net.sinedkadis.terracompositio.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCGameRules;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.registries.TCSounds;
import org.jetbrains.annotations.NotNull;

import static net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties.INFUSED;

public class WorldHelperInternal {
    public static @NotNull InteractionResult handleInWorldBlockCraft(BlockState oldState, BlockState newState, Level pLevel, BlockPos pPos, ItemStack item, int count) {
        float speed = 1 / 20f;
        return WorldHelper.handleInWorldBlockCraft(oldState, newState, pLevel, pPos, item, count, new ECFParticleData(speed), SoundEvents.COPPER_PLACE);
    }

    public static void flowLeak(BlockState pState, Level pLevel, BlockPos pPos) {
        if ((!pState.hasProperty(INFUSED) || pState.getValue(INFUSED))
                && !pLevel.getGameRules().getBoolean(TCGameRules.DISABLE_FLOW_LEAKING)
                && (!pState.hasProperty(TCBlockStateProperties.WAXED) || !pState.getValue(TCBlockStateProperties.WAXED))) {
            pLevel.playSound(null, pPos, TCSounds.FLOW_EVAPORATION.get(), SoundSource.BLOCKS);
            BlockPos f_pos;
            BlockPos b_pos;
            if (pState.hasProperty(RotatedPillarBlock.AXIS)) {
                f_pos = pPos.relative(pState.getValue(RotatedPillarBlock.AXIS), 1);
                b_pos = pPos.relative(pState.getValue(RotatedPillarBlock.AXIS), -1);
            } else {
                f_pos = pPos.relative(Direction.Axis.Y, 1);
                b_pos = pPos.relative(Direction.Axis.Y, -1);
            }


            BlockPos.betweenClosedStream(b_pos.offset(-1, -1, -1), f_pos.offset(1, 1, 1))
                    .filter(pos -> pos != pPos)
                    .filter(pos -> pLevel.getBlockState(pos).hasProperty(INFUSED))
                    .peek(pos -> BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                            .forEach(pos1 -> ParticleHelperInternal.spawnParticlesIn(pLevel, pos1)))
                    .forEach(pos -> pLevel.setBlockAndUpdate(pos, pLevel.getBlockState(pos).setValue(INFUSED, false)));
        }
    }

    public static boolean surroundedByFlow(Level level, BlockPos pos) {
        boolean upIsTrue = false;
        {
            BlockPos blockPos = pos.above();
            BlockState state = level.getBlockState(blockPos);

            if (!state.hasProperty(INFUSED) || !state.getValue(INFUSED)) {
                if (state.getFluidState().is(TCFluids.FLOW_FLUID.source.get())) {
                    upIsTrue = true;
                } else {
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER)
                            .orElse(EmptyFluidHandler.INSTANCE)
                            .drain(new FluidStack(TCFluids.FLOW_FLUID.source.get(), 1), IFluidHandler.FluidAction.SIMULATE).getAmount() > 0) {
                        upIsTrue = true;
                    }
                }
            } else upIsTrue = true;
        }
        if (!upIsTrue) return false;

        boolean downIsTrue = false;
        {
            BlockPos blockPos = pos.below();
            BlockState state = level.getBlockState(blockPos);

            if (!state.hasProperty(INFUSED) || !state.getValue(INFUSED)) {
                if (state.getFluidState().is(TCFluids.FLOW_FLUID.source.get())) {
                    downIsTrue = true;
                } else {
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER)
                            .orElse(EmptyFluidHandler.INSTANCE)
                            .drain(new FluidStack(TCFluids.FLOW_FLUID.source.get(), 1), IFluidHandler.FluidAction.SIMULATE).getAmount() > 0) {
                        downIsTrue = true;
                    }
                }
            } else downIsTrue = true;
        }

        return downIsTrue;
    }
}

package net.sinedkadis.terracompositio.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCGameRules;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.registries.TCSounds;
import org.jetbrains.annotations.NotNull;

import static net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties.INFUSED;

public class WorldHelperInternal {
    public static @NotNull ItemInteractionResult handleInWorldBlockCraft(BlockState oldState, BlockState newState, Level pLevel, BlockPos pPos, ItemStack item, int count) {
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

    private static final FluidStack fluidStack = new FluidStack(TCFluids.FLOW_FLUID.source.get(), 1);

    public static ITCRecipe.CraftException surroundedByFlow(Level level, BlockPos pos) {

        BlockPos above = pos.above();
        BlockPos below = pos.below();

        return containsFlow(level, above) && containsFlow(level, below) ?
                ITCRecipe.CraftException.OK : ITCRecipe.CraftException.NO_SURROUNDINGS;
    }

    public static boolean containsFlow(Level level, BlockPos blockPos) {
        boolean toReturn = false;
        BlockState state = level.getBlockState(blockPos);

        if (!state.hasProperty(INFUSED) || !state.getValue(INFUSED)) {
            if (state.getFluidState().is(TCFluids.FLOW_FLUID.source.get())) {
                toReturn = true;
            } else {
                IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, state, null, null);
                if (capability != null && capability
                        .drain(fluidStack, IFluidHandler.FluidAction.SIMULATE).getAmount() > 0) {
                    toReturn = true;
                }
            }
        } else toReturn = true;
        return toReturn;
    }
}

package net.sinedkadis.terracompositio.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.api.registries.TCGameRules;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import org.jetbrains.annotations.NotNull;

public class WorldHelperInternal {
    public static @NotNull InteractionResult handleInWorldBlockCraft(BlockState oldState, BlockState newState, Level pLevel, BlockPos pPos, ItemStack item, int count) {
        float speed = 1 / 20f;
        return WorldHelper.handleInWorldBlockCraft(oldState, newState, pLevel, pPos, item, count, new ECFParticleData(speed), SoundEvents.COPPER_PLACE);
    }

    public static void flowLeak(BlockState pState, Level pLevel, BlockPos pPos) {
        if ((!pState.hasProperty(TCBlockStateProperties.INFUSED) || pState.getValue(TCBlockStateProperties.INFUSED))
                && !pLevel.getGameRules().getBoolean(TCGameRules.DISABLE_FLOW_LEAKING)
                && (!pState.hasProperty(TCBlockStateProperties.WAXED) || !pState.getValue(TCBlockStateProperties.WAXED))) {

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
                    .filter(pos -> pLevel.getBlockState(pos).hasProperty(TCBlockStateProperties.INFUSED))
                    .forEach(pos -> pLevel.setBlockAndUpdate(pos, pLevel.getBlockState(pos).setValue(TCBlockStateProperties.INFUSED, false)));
        }
    }
}

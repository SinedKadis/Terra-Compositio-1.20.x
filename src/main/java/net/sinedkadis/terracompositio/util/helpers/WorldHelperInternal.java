package net.sinedkadis.terracompositio.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.particle.ECFParticleData;
import org.jetbrains.annotations.NotNull;

public class WorldHelperInternal {
    public static @NotNull ItemInteractionResult handleInWorldBlockCraft(BlockState oldState, BlockState newState, Level pLevel, BlockPos pPos, ItemStack item, int count) {
        float speed = 1 / 20f;
        return WorldHelper.handleInWorldBlockCraft(oldState, newState, pLevel, pPos, item, count, new ECFParticleData(speed), SoundEvents.COPPER_PLACE);
    }
}

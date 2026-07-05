package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.sinedkadis.terracompositio.registries.TCFluids;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
public class BirchJuiceCauldronBlock extends TCCauldronBlock {


    public BirchJuiceCauldronBlock(Properties pProperties, Biome.Precipitation precipitation, CauldronInteraction.InteractionMap pInteractions) {
        super(pProperties, precipitation, pInteractions);
    }

    @Override
    public boolean canReceiveWedgeDrip(Fluid fluid) {
        return fluid == TCFluids.BIRCH_JUICE_FLUID.source.get();
    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (state.getValue(LEVEL) == 3) {
            if (itemStack.getItem() == Items.BUCKET) {
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 1);
                if (itemStack.getCount() > 1 || player.isCreative()) {
                    if (!player.addItem(new ItemStack(TCFluids.BIRCH_JUICE_FLUID.bucket.get()))) {
                        player.drop(new ItemStack(TCFluids.BIRCH_JUICE_FLUID.bucket.get()), false);
                    }
                    if (!player.isCreative()) {
                        itemStack.setCount(itemStack.getCount() - 1);
                    }
                } else {
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(TCFluids.BIRCH_JUICE_FLUID.bucket.get()));
                    }
                }
                player.playSound(SoundEvents.BUCKET_FILL);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

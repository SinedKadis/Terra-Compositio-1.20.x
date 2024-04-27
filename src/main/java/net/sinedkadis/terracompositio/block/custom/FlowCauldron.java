package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sinedkadis.terracompositio.fluid.ModFluids;

import java.util.Map;

public class FlowCauldron extends AbstractCauldronBlock{
    public FlowCauldron(Properties pProperties, Map<Item, CauldronInteraction> pInteractions) {
        super(pProperties, pInteractions);
    }

    @Override
    public boolean isFull(BlockState pState) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);

        if (itemStack.getItem() == Items.BUCKET){
            pLevel.setBlock(pPos, Blocks.CAULDRON.defaultBlockState(),1);
            if (pPlayer.isCreative()){
                pPlayer.addItem(new ItemStack(ModFluids.FLOW_FLUID.bucket.get()));
            }else {
                if (itemStack.getCount()>1){
                    pPlayer.addItem(new ItemStack(ModFluids.FLOW_FLUID.bucket.get()));
                    itemStack.setCount(itemStack.getCount()-1);
                }else {
                    pPlayer.setItemInHand(pHand,new ItemStack(ModFluids.FLOW_FLUID.bucket.get()));
                }
            }
            pPlayer.playSound(SoundEvents.BUCKET_FILL);
            return InteractionResult.SUCCESS;
        }


        return InteractionResult.PASS;
    }
}

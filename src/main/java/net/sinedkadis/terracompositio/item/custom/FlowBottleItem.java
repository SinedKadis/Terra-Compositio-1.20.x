package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.block.ModBlocks;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

public class FlowBottleItem extends Item {
    public FlowBottleItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack itemStack = pContext.getItemInHand();
        BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (blockState.hasProperty(LEVEL)){
            int levelValue = blockState.getValue(LEVEL);
            if (itemStack.getCount()==1){
                if (levelValue !=3) {
                    pContext.getLevel().setBlock(pContext.getClickedPos(),blockState.setValue(LEVEL, levelValue + 1),1);
                    pContext.getPlayer().setItemInHand(pContext.getHand(),new ItemStack(Items.GLASS_BOTTLE));
                    pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                    return InteractionResult.SUCCESS;
                }
            }else {
                if (levelValue !=3) {
                    pContext.getLevel().setBlock(pContext.getClickedPos(),blockState.setValue(LEVEL, levelValue + 1),1);
                    if (!pContext.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE))){
                        pContext.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE),false);
                    }
                    pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                    return InteractionResult.SUCCESS;
                }
            }

        }else if (blockState == Blocks.CAULDRON.defaultBlockState()){
            pContext.getLevel().setBlock(pContext.getClickedPos(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(LEVEL,1),1);
            if (itemStack.getCount()==1){
                pContext.getPlayer().setItemInHand(pContext.getHand(),new ItemStack(Items.GLASS_BOTTLE));
                pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                return InteractionResult.SUCCESS;
            }else {
                if (!pContext.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE))){
                    pContext.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE),false);
                }
                pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}

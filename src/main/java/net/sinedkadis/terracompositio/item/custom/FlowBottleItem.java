package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.effect.ModEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

public class FlowBottleItem extends Item {
    public FlowBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        pEntityLiving.addEffect(new MobEffectInstance(ModEffects.FLOW_SATURATION.get(),200));
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (pStack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        pEntityLiving.gameEvent(GameEvent.DRINK);
        return pStack;
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

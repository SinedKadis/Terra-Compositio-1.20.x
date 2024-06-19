package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.effect.ModEffects;
import net.sinedkadis.terracompositio.item.ModArmorMaterials;
import net.sinedkadis.terracompositio.item.ModItems;
import org.jetbrains.annotations.NotNull;

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
        if (player != null
                && hasCorrectArmorOn(ModArmorMaterials.NONFLOW_WOOD,player)){

            float bootsDamagePercentage = (float) getDamage(player.getInventory().getArmor(0)) / player.getInventory().getArmor(0).getMaxDamage();
            float leggingsDamagePercentage = (float) getDamage(player.getInventory().getArmor(1)) / player.getInventory().getArmor(1).getMaxDamage();
            float chestplateDamagePercentage = (float) getDamage(player.getInventory().getArmor(2)) / player.getInventory().getArmor(2).getMaxDamage();
            float helmetDamagePercentage = (float) getDamage(player.getInventory().getArmor(3)) / player.getInventory().getArmor(3).getMaxDamage();

            float[] tags = new float[]{
                    bootsDamagePercentage,
                    leggingsDamagePercentage,
                    chestplateDamagePercentage,
                    helmetDamagePercentage,
            };

            ItemStack boots = new ItemStack(((FlowArmorItem) ModItems.FLOW_WOOD_BOOTS.get())
                    .setOldDamage(tags));
            boots.setTag(player.getInventory().getArmor(0).getTag());
            boots.setDamageValue((int) (bootsDamagePercentage * boots.getMaxDamage()));

            ItemStack leggings = new ItemStack(((FlowArmorItem) ModItems.FLOW_WOOD_LEGGINGS.get())
                    .setOldDamage(tags));
            leggings.setTag(player.getInventory().getArmor(1).getTag());
            leggings.setDamageValue((int) (leggingsDamagePercentage * leggings.getMaxDamage()));

            ItemStack chestplate = new ItemStack(((FlowArmorItem) ModItems.FLOW_WOOD_CHESTPLATE.get())
                    .setOldDamage(tags));
            chestplate.setTag(player.getInventory().getArmor(2).getTag());
            chestplate.setDamageValue((int) (chestplateDamagePercentage * chestplate.getMaxDamage()));

            ItemStack helmet = new ItemStack(((FlowArmorItem) ModItems.FLOW_WOOD_HELMET.get())
                    .setOldDamage(tags));
            helmet.setTag(player.getInventory().getArmor(3).getTag());
            helmet.setDamageValue((int) (helmetDamagePercentage * helmet.getMaxDamage()));

            player.setItemSlot(EquipmentSlot.FEET,boots);
            player.setItemSlot(EquipmentSlot.LEGS,leggings);
            player.setItemSlot(EquipmentSlot.CHEST,chestplate);
            player.setItemSlot(EquipmentSlot.HEAD,helmet);
        }else {
            pEntityLiving.addEffect(new MobEffectInstance(ModEffects.FLOW_SATURATION.get(),200));
        }
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
            }else {
                if (!pContext.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE))){
                    pContext.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE),false);
                }
            }
            pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem)player.getInventory().getArmor(0).getItem());
        ArmorItem leggings = ((ArmorItem)player.getInventory().getArmor(1).getItem());
        ArmorItem breastplate = ((ArmorItem)player.getInventory().getArmor(2).getItem());
        ArmorItem helmet = ((ArmorItem)player.getInventory().getArmor(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }

}

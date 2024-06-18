package net.sinedkadis.terracompositio.item.custom;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.sinedkadis.terracompositio.item.ModItems;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class FlowArmorItem extends ModArmorItem{
    private static final Logger LOGGER = LogUtils.getLogger();
    private float[] damage;
    public FlowArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    public FlowArmorItem setOldDamage(float[] damage){
        this.damage = damage;
        return this;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        //LOGGER.debug("Method called");
        int currentDurability = stack.getMaxDamage() - stack.getItem().getDamage(stack);
        if (amount >= currentDurability){
            //LOGGER.debug("{} is more than {}", amount, currentDurability);
            if(entity instanceof ServerPlayer pPlayer) {
                //LOGGER.debug("Entity is Server player");
                ItemStack oldBoots = new ItemStack(ModItems.NONFLOW_WOOD_BOOTS.get());
                ItemStack oldLeggings = new ItemStack(ModItems.NONFLOW_WOOD_LEGGINGS.get());
                ItemStack oldChestplate = new ItemStack(ModItems.NONFLOW_WOOD_CHESTPLATE.get());
                ItemStack oldHelmet = new ItemStack(ModItems.NONFLOW_WOOD_HELMET.get());

                oldBoots.setTag(pPlayer.getInventory().getArmor(0).getTag());
                oldLeggings.setTag(pPlayer.getInventory().getArmor(1).getTag());
                oldChestplate.setTag(pPlayer.getInventory().getArmor(2).getTag());
                oldHelmet.setTag(pPlayer.getInventory().getArmor(3).getTag());

                oldBoots.setDamageValue((int) (damage[0] * oldBoots.getMaxDamage()));
                oldLeggings.setDamageValue((int) (damage[1] * oldLeggings.getMaxDamage()));
                oldChestplate.setDamageValue((int) (damage[2] * oldChestplate.getMaxDamage()));
                oldHelmet.setDamageValue((int) (damage[3] * oldHelmet.getMaxDamage()));

                pPlayer.setItemSlot(EquipmentSlot.FEET,oldBoots);
                pPlayer.setItemSlot(EquipmentSlot.LEGS,oldLeggings);
                pPlayer.setItemSlot(EquipmentSlot.CHEST,oldChestplate);
                pPlayer.setItemSlot(EquipmentSlot.HEAD,oldHelmet);
                return 0;
            }// else LOGGER.debug("No");
        } //else LOGGER.debug("{} is less than {}", amount, currentDurability);
        return amount;
    }

}

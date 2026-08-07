package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.registries.TCArmorMaterials;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CedarArmorItem extends TCArmorItem {

    private static final String oldDamage = "oldDamagePercent";

    @Override
    public @NotNull Type getType() {
        return type;
    }

    private final Type type;

    public CedarArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
        this.type = pType;
    }

    public static ItemStack setOldDamage(ItemStack item, int damage) {
        item.getOrCreateTag().putInt(oldDamage, damage);
        return item;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int currentDurability = stack.getMaxDamage() - stack.getItem().getDamage(stack);
        if (amount >= currentDurability) {
            if (entity instanceof ServerPlayer pPlayer) {
                setNonFlowArmorBack(pPlayer, this.type.getSlot().getIndex(), true);
                return 0;
            }
        }
        return amount;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (!pLevel.isClientSide()
                && pEntity instanceof Player pPlayer) {
            if (!pPlayer.getItemBySlot(LivingEntity.getEquipmentSlotForItem(pStack)).equals(pStack)) {
                this.setNonFlowArmorBack(pPlayer, pSlotId, false);
            }
            if (!hasCorrectArmorOn(TCArmorMaterials.FLOWING_FLOW_CEDAR, pPlayer)) {
                setArmorInSlotBackIfMatch(pPlayer, EquipmentSlot.FEET, TCItems.FLOWING_FLOW_CEDAR_BOOTS, TCItems.FLOW_CEDAR_BOOTS);
                setArmorInSlotBackIfMatch(pPlayer, EquipmentSlot.LEGS, TCItems.FLOWING_FLOW_CEDAR_LEGGINGS, TCItems.FLOW_CEDAR_LEGGINGS);
                setArmorInSlotBackIfMatch(pPlayer, EquipmentSlot.CHEST, TCItems.FLOWING_FLOW_CEDAR_CHESTPLATE, TCItems.FLOW_CEDAR_CHESTPLATE);
                setArmorInSlotBackIfMatch(pPlayer, EquipmentSlot.HEAD, TCItems.FLOWING_FLOW_CEDAR_HELMET, TCItems.FLOW_CEDAR_HELMET);
            }
        }
    }


    private void setNonFlowArmorBack(Player pPlayer, int slotID, boolean inArmorSlot) {
        setCurrentArmorInSlotBackIfMatch(EquipmentSlot.FEET, TCItems.FLOW_CEDAR_BOOTS, pPlayer, slotID, inArmorSlot);
        setCurrentArmorInSlotBackIfMatch(EquipmentSlot.LEGS, TCItems.FLOW_CEDAR_LEGGINGS, pPlayer, slotID, inArmorSlot);
        setCurrentArmorInSlotBackIfMatch(EquipmentSlot.CHEST, TCItems.FLOW_CEDAR_CHESTPLATE, pPlayer, slotID, inArmorSlot);
        setCurrentArmorInSlotBackIfMatch(EquipmentSlot.HEAD, TCItems.FLOW_CEDAR_HELMET, pPlayer, slotID, inArmorSlot);
    }

    public void setArmorInSlotBackIfMatch(Player pPlayer, EquipmentSlot feet, RegistryObject<Item> oldItem, RegistryObject<Item> newItem) {
        if (pPlayer.getItemBySlot(feet).getItem() == oldItem.get()) {
            ItemStack stack = new ItemStack(newItem.get());
            stack.setTag(pPlayer.getItemBySlot(feet).getTag());
            int oldDamage = stack.getOrCreateTag().getInt(CedarArmorItem.oldDamage);
            stack.setDamageValue(oldDamage);
            pPlayer.setItemSlot(feet, stack);
        }
    }

    public void setCurrentArmorInSlotBackIfMatch(EquipmentSlot slot, RegistryObject<Item> newItem, Player pPlayer, int slotID, boolean inArmorSlot) {
        if (this.type.getSlot() == slot) {
            ItemStack stack = new ItemStack(newItem.get());
            ItemStack old;
            if (inArmorSlot) {
                old = pPlayer.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slotID));
            } else {
                old = pPlayer.getInventory().getItem(slotID);
            }
            int oldDamage = old.getOrCreateTag().getInt(CedarArmorItem.oldDamage);
            stack.setTag(old.getTag());
            stack.setDamageValue(oldDamage);
            if (inArmorSlot) {
                pPlayer.getInventory().armor.set(slotID, stack);
            } else {
                pPlayer.getInventory().setItem(slotID, stack);
            }
        }
    }
 /*
    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        ItemStack stack = new ItemStack(ModItems.NONFLOW_WOOD_HELMET.get());
        stack.setTag(item.getTag());
        stack.setDamageValue((int) (damage[3] * stack.getMaxDamage()));
        player.drop(stack,true);
        return true;
    }

 */
}

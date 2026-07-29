package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.registries.TCArmorMaterials;
import net.sinedkadis.terracompositio.registries.TCDataComponents;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Consumer;

public class CedarArmorItem extends TCArmorItem{

    @Override
    public @NotNull Type getType() {
        return type;
    }

    private final Type type;

    public CedarArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
        this.type = pType;
    }

    public static ItemStack setOldDamage(@UnknownNullability ItemStack item, int damage) {
        item.set(TCDataComponents.OLD_DAMAGE,damage);
        return item;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, @NotNull Consumer<Item> onBroken) {
        int currentDurability = stack.getMaxDamage() - stack.getItem().getDamage(stack);
        if (amount >= currentDurability){
            if(entity instanceof ServerPlayer pPlayer) {
                setNonFlowArmorBack(pPlayer,this.type.getSlot().getIndex(),true);
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
            if (!pPlayer.getItemBySlot(pPlayer.getEquipmentSlotForItem(pStack)).equals(pStack)) {
                this.setNonFlowArmorBack(pPlayer,pSlotId,false);
            }

            if (!hasCorrectArmorOn(TCArmorMaterials.FLOWING_FLOW_CEDAR.value(),pPlayer)) {
                ItemStack boots = pPlayer.getItemBySlot(EquipmentSlot.FEET);
                ItemStack leggings = pPlayer.getItemBySlot(EquipmentSlot.LEGS);
                ItemStack chestplate = pPlayer.getItemBySlot(EquipmentSlot.CHEST);
                ItemStack helmet = pPlayer.getItemBySlot(EquipmentSlot.HEAD);

                if (boots.getItem() == TCItems.FLOWING_FLOW_CEDAR_BOOTS.get()) {
                    ItemStack stack = new ItemStack(TCItems.FLOW_CEDAR_BOOTS.get());
                    DataComponentMap components = pPlayer.getItemBySlot(EquipmentSlot.FEET).getComponents();
                    DataComponentMap newComponents = DataComponentMap.builder()
                            .addAll(components)
                            .set(DataComponents.MAX_DAMAGE, stack.get(DataComponents.MAX_DAMAGE))
                            .build();
                    stack.applyComponents(newComponents);
                    Integer damage = stack.get(TCDataComponents.OLD_DAMAGE);
                    if (damage == null) damage = stack.getMaxDamage();
                    stack.setDamageValue(damage);
                    pPlayer.setItemSlot(EquipmentSlot.FEET, stack);
                }
                if (leggings.getItem() == TCItems.FLOWING_FLOW_CEDAR_LEGGINGS.get()) {
                    ItemStack stack = new ItemStack(TCItems.FLOW_CEDAR_LEGGINGS.get());
                    DataComponentMap components = pPlayer.getItemBySlot(EquipmentSlot.LEGS).getComponents();
                    DataComponentMap newComponents = DataComponentMap.builder()
                            .addAll(components)
                            .set(DataComponents.MAX_DAMAGE, stack.get(DataComponents.MAX_DAMAGE))
                            .build();
                    stack.applyComponents(newComponents);
                    Integer damage = stack.get(TCDataComponents.OLD_DAMAGE);
                    if (damage == null) damage = stack.getMaxDamage();
                    stack.setDamageValue(damage);
                    pPlayer.setItemSlot(EquipmentSlot.LEGS, stack);
                }
                if (chestplate.getItem() == TCItems.FLOWING_FLOW_CEDAR_CHESTPLATE.get()) {
                    ItemStack stack = new ItemStack(TCItems.FLOW_CEDAR_CHESTPLATE.get());
                    DataComponentMap components = pPlayer.getItemBySlot(EquipmentSlot.CHEST).getComponents();
                    DataComponentMap newComponents = DataComponentMap.builder()
                            .addAll(components)
                            .set(DataComponents.MAX_DAMAGE, stack.get(DataComponents.MAX_DAMAGE))
                            .build();
                    stack.applyComponents(newComponents);
                    Integer damage = stack.get(TCDataComponents.OLD_DAMAGE);
                    if (damage == null) damage = stack.getMaxDamage();
                    stack.setDamageValue(damage);
                    pPlayer.setItemSlot(EquipmentSlot.CHEST, stack);
                }
                if (helmet.getItem() == TCItems.FLOWING_FLOW_CEDAR_HELMET.get()) {
                    ItemStack stack = new ItemStack(TCItems.FLOW_CEDAR_HELMET.get());
                    DataComponentMap components = pPlayer.getItemBySlot(EquipmentSlot.HEAD).getComponents();
                    DataComponentMap newComponents = DataComponentMap.builder()
                            .addAll(components)
                            .set(DataComponents.MAX_DAMAGE, stack.get(DataComponents.MAX_DAMAGE))
                            .build();
                    stack.applyComponents(newComponents);
                    Integer damage = stack.get(TCDataComponents.OLD_DAMAGE);
                    if (damage == null) damage = stack.getMaxDamage();
                    stack.setDamageValue(damage);
                    pPlayer.setItemSlot(EquipmentSlot.HEAD, stack);
                }
            }
        }
    }

    private void setNonFlowArmorBack(Player pPlayer,int slotID,boolean inArmorSlot){
        ItemStack stack = ItemStack.EMPTY;
        switch (this.type.getSlot()) {
            case FEET -> stack = new ItemStack(TCItems.FLOW_CEDAR_BOOTS.get());
            case LEGS -> stack = new ItemStack(TCItems.FLOW_CEDAR_LEGGINGS.get());
            case CHEST -> stack = new ItemStack(TCItems.FLOW_CEDAR_CHESTPLATE.get());
            case HEAD -> stack = new ItemStack(TCItems.FLOW_CEDAR_HELMET.get());
        }
        if (stack == ItemStack.EMPTY) return;

        ItemStack item;
        if (!inArmorSlot)
            item = pPlayer.getInventory().getItem(slotID);
        else
            item = pPlayer.getInventory().armor.get(slotID);
        DataComponentMap components = item.getComponents();
        DataComponentMap newComponents = DataComponentMap.builder()
                .addAll(components)
                .set(DataComponents.MAX_DAMAGE, stack.get(DataComponents.MAX_DAMAGE))
                .build();
        stack.applyComponents(newComponents);
        Integer damage = item.get(TCDataComponents.OLD_DAMAGE);
        if (damage == null) damage = stack.getMaxDamage();
        stack.setDamageValue(damage);
        if (inArmorSlot){
            pPlayer.getInventory().armor.set(slotID,stack);
        }else {
            pPlayer.getInventory().setItem(slotID,stack);
        }
    }
}

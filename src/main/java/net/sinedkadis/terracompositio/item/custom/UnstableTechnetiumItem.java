package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.IECFStorageExtensionItem;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.registries.TCTags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class UnstableTechnetiumItem extends Item implements IECFStorageExtensionItem {
    private final int radiation;
    private final IECFStorageExtensionItem storageExt;

    public UnstableTechnetiumItem(Properties pProperties, int radiation, IECFStorageExtensionItem storageExt) {
        super(pProperties);
        this.radiation = radiation;
        this.storageExt = storageExt;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (pEntity instanceof LivingEntity entity){
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER,200,radiation,false,false));
        }

    }


    @SubscribeEvent
    public static void onPlayerTickEvent(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        Level level = player.level();
        Inventory inventory= player.getInventory();
        List<ItemStack> containers = inventory.items.stream().filter(itemStack -> itemStack.is(Items.BUNDLE) || itemStack.is(Items.SHULKER_BOX)).toList();
        for (ItemStack container : containers){
            Stream<ItemStack> stream;
            if (container.is(Items.BUNDLE)) {
                stream = ShieldedBundleItem.getContents(container);
            } else {
                stream = ItemHelper.getContainerContents(container);
            }
            stream.filter(itemStack -> itemStack.is(TCTags.Items.UNSTABLE_TECHNETIUM))
                    .forEach(itemStack -> itemStack.inventoryTick(level,player,-1,true));
        }
    }

    @Override
    public int maxStorage() {
        return storageExt.maxStorage();
    }
}

package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.registries.TCTags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class ShieldedBundleItem extends BundleItem {

    private static final int BAR_COLOR = Mth.color(49/255F, 111/255F, 125/255F);

    public ShieldedBundleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        if (getContents(stack).findAny().isPresent()){
            return 1;
        }
        return 64;
    }

    public static Stream<ItemStack> getContents(ItemStack pStack) {
        BundleContents bundleContents = pStack.get(DataComponents.BUNDLE_CONTENTS);
        return bundleContents == null ? Stream.of() : bundleContents.itemCopyStream();
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack pStack, ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess) {
        if (pOther.is(TCTags.Items.UNSTABLE_TECHNETIUM) || pOther.isEmpty()) {
            return super.overrideOtherStackedOnMe(pStack, pOther, pSlot, pAction, pPlayer, pAccess);
        }
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer) {
        if (!pStack.is(TCTags.Items.UNSTABLE_TECHNETIUM))
            return false;
        return super.overrideStackedOnOther(pStack, pSlot, pAction, pPlayer);
    }

    @Override
    public int getBarColor(@NotNull ItemStack pStack) {
        return BAR_COLOR;
    }

    @SubscribeEvent
    public static void onItemPickUpEvent(ItemEntityPickupEvent.Post event) {
        ItemStack stack = event.getOriginalStack();
        if (stack.is(TCTags.Items.UNSTABLE_TECHNETIUM)){
            Player player = event.getPlayer();
            Inventory inventory = player.getInventory();
            ItemStack newBundle = TCItems.SHIELDED_BUNDLE.get().getDefaultInstance();
            List<ItemStack> bundles = inventory.items.stream().filter(itemStack -> itemStack.is(TCItems.SHIELDED_BUNDLE.get())).toList();
            for (ItemStack bundle : bundles){
                int added;
                if (bundle.getCount() > 1){
                    bundle.shrink(1);
                    BundleContents bundlecontents = new BundleContents(List.of(stack));
                    newBundle.set(DataComponents.BUNDLE_CONTENTS,bundlecontents);
                    added = stack.getCount();
                    if (!player.addItem(newBundle)){
                        player.drop(newBundle,false);
                    }
                } else {
                    BundleContents bundlecontents = bundle.get(DataComponents.BUNDLE_CONTENTS);
                    if (bundlecontents == null) continue;
                    BundleContents.Mutable mutable = new BundleContents.Mutable(bundlecontents);
                    added = mutable.tryInsert(stack);
                    bundle.set(DataComponents.BUNDLE_CONTENTS,mutable.toImmutable());
                }
                if (added != 0){
                    int itemSlot = inventory.findSlotMatchingItem(stack);
                    if (itemSlot >= 0){
                        ItemStack item = inventory.getItem(itemSlot);
                        item.shrink(added);
                        if (item.isEmpty()){
                            break;
                        }
                    }
//                    else {
//                        dupe)
//                    }
                }
            }

        }
    }


}

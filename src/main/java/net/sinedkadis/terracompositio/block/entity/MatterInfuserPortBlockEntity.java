package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.tooltip.ItemComponent;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MatterInfuserPortBlockEntity extends MatterInfuserBaseBlockEntity {
    public MatterInfuserPortBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TCBlockEntities.MATTER_INFUSER_PORT_BE.get(),pPos, pBlockState);
    }

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {

    }

    @Override
    public void collectKnowledgeData(CompoundTag data) {
        FlowCedarCasingBlockEntity casingBE = getCasingBE();
        if (casingBE == null) return;
        IItemHandler itemHandler = casingBE.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(EmptyHandler.INSTANCE);
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            list.add(itemHandler.getStackInSlot(i));
        }
        data.put("inventory", ItemHelper.writeItemList(list));
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.ITEMS, tooltip, t -> {
            List<ItemStack> entries = ItemHelper.readItemList(data.getList("inventory", Tag.TAG_COMPOUND));
            for (ItemStack stack : entries) {
                if (stack.isEmpty()) continue;
                t.add(ItemComponent.of(stack));

            }
        });
    }
}

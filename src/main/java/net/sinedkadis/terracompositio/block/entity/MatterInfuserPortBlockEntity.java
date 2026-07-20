package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
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
    protected IItemHandlerModifiable getItemHandler() {
        FlowCedarCasingBlockEntity casingBE = getCasingBE();
        if (casingBE != null) {
            return casingBE.getItemHandler();
        }
        return ((IItemHandlerModifiable) EmptyItemHandler.INSTANCE);
    }

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {

    }

    int timer = 0;
    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        if (timer <= 0) {
            timer = 5;
            playSoundIfNeeded(pLevel, pPos);
        }
        --timer;
    }

    @Override
    protected void playSoundIfNeeded(Level level, BlockPos pos) {
        Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise();
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.relative(direction),pos.relative(direction,8))) {
            BlockEntity blockEntity = level.getBlockEntity(blockpos);
            if (blockEntity instanceof MatterInfuserUnitBlockEntity unitBlockEntity) {
                if (unitBlockEntity.progress > 0) {
                    level.playSound(null, blockpos, SoundEvents.AZALEA_STEP, SoundSource.BLOCKS);
                    return;
                }
            }
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        FlowCedarCasingBlockEntity casingBE = getCasingBE();
        if (casingBE == null) return;
        IItemHandler itemHandler = casingBE.getItemCapability(null);
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            list.add(itemHandler.getStackInSlot(i));
        }
        data.put("inventory", ItemHelper.writeItemList(list, provider));
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.ITEMS, tooltip, t -> {
            List<ItemStack> entries = ItemHelper.readItemList(data.getList("inventory", Tag.TAG_COMPOUND), provider);
            for (ItemStack stack : entries) {
                if (stack.isEmpty()) continue;
                t.add(ItemComponent.of(stack));

            }
        });
    }

    @Override
    protected int getECF() {
        return 0;
    }
}

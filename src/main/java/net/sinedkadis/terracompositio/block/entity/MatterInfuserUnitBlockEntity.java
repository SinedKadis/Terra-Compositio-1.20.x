package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.registries.TCCapabilities;
import net.sinedkadis.terracompositio.api.tooltip.ItemComponent;
import net.sinedkadis.terracompositio.block.behaviours.CraftingBehaviour;
import net.sinedkadis.terracompositio.block.behaviours.ECFHandlerBehaviour;
import net.sinedkadis.terracompositio.block.behaviours.ItemStateHolderBehaviour;
import net.sinedkadis.terracompositio.block.custom.MatterInfuserBaseEntityBlock;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.recipe.MatterInfusionRecipe;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity.DOWN_CONNECTION_SLOT;
import static net.sinedkadis.terracompositio.block.entity.FlowCedarCasingBlockEntity.UP_CONNECTION_SLOT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MatterInfuserUnitBlockEntity extends MatterInfuserBaseBlockEntity{


    public MatterInfuserUnitBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.MATTER_INFUSER_IO_BE.get(), pos, state);
    }

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {
        list.add(new ECFHandlerBehaviour(this)
                .range(10)
                .priority(TCInnerConfig.DEFAULT_CONSUMER_PRIORITY));
        list.add(new ItemStateHolderBehaviour(this) {

            @Override
            public int getLimitInSlot(int slot) {
                return 2;
            }

            @Override
            public boolean allowInsert(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
                boolean enough = pStack.getCount() >= 2;
                boolean isRod = pStack.is(TCItems.INFUSED_IRON_ROD.get());
                boolean slotIsEmpty = itemHandler.getStackInSlot(pSlot).isEmpty();
                Direction left = getBlockEntity().getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
                if (level == null) return false;
                BlockState leftState = level.getBlockState(getBlockPos().relative(left));
                boolean leftIsMI = leftState.getBlock() instanceof MatterInfuserBaseEntityBlock;
                return manual && enough && isRod && slotIsEmpty && leftIsMI;
            }

            @Override
            public InteractionResult onUse(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
                TCBlockEntity blockEntity = getBlockEntity();
                Level level = blockEntity.getLevel();
                if (level != null) {
                    BlockPos blockPos = blockEntity.getBlockPos();
                    Direction left = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
                    if (level.getBlockState(blockPos.relative(left)).getBlock() instanceof MatterInfuserBaseEntityBlock)
                        return super.onUse(pPlayer, pHand, pHit);
                }
                return InteractionResult.PASS;
            }
        });
        list.add(new CraftingBehaviour<>(this, MatterInfusionRecipe.Type.INSTANCE));

    }

    public boolean assembleValid() {
        if (level == null) return false;

        FlowCedarCasingBlockEntity casingBE = getCasingBE();

        if (casingBE == null) {
            return false;
        }
        IItemHandler casingItemHandler = casingBE.getCapability(TCCapabilities.ITEM_STATE_HOLDER).orElse(((IItemHandlerModifiable) EmptyHandler.INSTANCE));
        if (casingItemHandler.getStackInSlot(UP_CONNECTION_SLOT).isEmpty()
                || casingItemHandler.getStackInSlot(DOWN_CONNECTION_SLOT).isEmpty())
            return false;

        for (int i = 0; i < 9; i++) {
            Direction dir = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
            BlockPos currentPos = worldPosition.relative(dir, i);
            BlockEntity blockEntity = level.getBlockEntity(currentPos);
            if (blockEntity instanceof MatterInfuserPortBlockEntity) break;
            if (blockEntity instanceof MatterInfuserUnitBlockEntity unitBlockEntity) {
                IItemHandler unitItemHandler = unitBlockEntity.getCapability(TCCapabilities.ITEM_STATE_HOLDER).orElse(((IItemHandlerModifiable) EmptyHandler.INSTANCE));
                if (unitItemHandler.getStackInSlot(0).isEmpty()) return false;
                continue;
            }
            return false;
        }
        return true;
    }


    public ItemStack getCatalyst() {
        MatterInfuserPortBlockEntity port = this.getPortBE();
        return port != null ? port.getInputSlot() : ItemStack.EMPTY;
    }

    @Nullable
    public MatterInfuserPortBlockEntity getPortBE() {
        for (int i = 1; i <= 8; i++){
            if (level != null
                    && level.getBlockEntity(worldPosition.relative(
                            this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise(), i))
                    instanceof MatterInfuserPortBlockEntity blockEntity) {
                if (blockEntity.getInputSlot().isEmpty())
                    continue;
                return blockEntity;
            }
        }
        return null;
    }

    @Override
    public void collectKnowledgeData(CompoundTag data) {
        FlowCedarCasingBlockEntity casingBE = getCasingBE();
        if (casingBE == null) return;
        IItemHandler itemHandler = casingBE.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(SentinelHelper.EMPTY_ITEM_HANDLER);
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            list.add(itemHandler.getStackInSlot(i));
        }
        data.put("inventory", ItemHelper.writeItemList(list));
        super.collectKnowledgeData(data);
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting) {
        super.addTooltipLines(data, tooltip, isShifting);
        TooltipHelper.addWithHeader(TooltipHelper.Headers.ITEMS, tooltip, t -> {
            List<ItemStack> entries = ItemHelper.readItemList(data.getList("inventory", Tag.TAG_COMPOUND));
            for (ItemStack stack : entries) {
                if (stack.isEmpty()) continue;
                t.add(ItemComponent.of(stack));

            }
        });
    }
}

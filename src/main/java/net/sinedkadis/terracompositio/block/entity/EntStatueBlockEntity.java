package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetwork;
import net.sinedkadis.terracompositio.api.networks.fluid.FluidNetworkMember;
import net.sinedkadis.terracompositio.block.behaviours.ItemHandlerBehaviour;
import net.sinedkadis.terracompositio.entity.custom.FlowCedarEntEntity;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.registries.TCEntities;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EntStatueBlockEntity extends TCBlockEntity implements FluidNetworkMember {

    private final EmptyFluidHandler fluidHandler = new EmptyFluidHandler() {
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid().isSame(TCFluids.FLOW_FLUID.source.get()) && resource.getAmount() == 1000) {
                if (action.execute()) {
                    jojoReference();
                }
                return 1000;
            }
            return super.fill(resource, action);
        }
    };

    public EntStatueBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.ENT_STATUE_BE.get(), pos, state);
    }



    int cd = 20;

    @Override
    public void addBEBehaviours(List<IBEBehaviour> list) {
        list.add(new ItemHandlerBehaviour(this) {
            @Override
            public boolean allowInsert(int pSlot, ItemStack pStack, @Nullable Direction pDirection, boolean manual) {
                return pStack.is(TCItems.TECHNETIUM_CROWN.get());
            }
        });
    }

    @Override
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        super.tick(pLevel, pPos, pState);
        FluidNetwork fluidNetworkInstance = TerraCompositioAPI.INSTANCE.getFluidNetworkInstance();
        boolean inNetwork = fluidNetworkInstance.isIn(level, this);
        if (!inNetwork && !this.isRemoved()) {
            fluidNetworkInstance.fireFluidNetworkEvent(this, NetworkAction.ADD);
        }
        if (cd<= 0) {
            fluidNetworkInstance.updateInRange(pLevel, pPos, getRange());
            cd = 20;
        }
        cd--;
        updateIfScheduled();
    }

    public void jojoReference() {
        if (level instanceof ServerLevel) {
            FlowCedarEntEntity pEntity = TCEntities.FLOW_CEDAR_ENT.get().create(level);
            if (pEntity != null) {
                level.addFreshEntity(pEntity);
                pEntity.getInnerECFHandler().setECF(30);
                ItemStack crown = this.itemHandler().getStackInSlot(0);
                if (crown.is(TCItems.TECHNETIUM_CROWN.get())) {
                    pEntity.setItemSlot(EquipmentSlot.HEAD,crown.copyAndClear());
                }
                BlockPos blockPos = getBlockPos();
                pEntity.setPos(blockPos.getCenter());
                level.destroyBlock(blockPos,false);
                TerraCompositioAPI.instance().getFluidNetworkInstance()
                        .fireFluidNetworkEvent(this,NetworkAction.REMOVE);
                //this.setRemoved();
            }
        }
    }

    private IItemHandler itemHandler() {
        return ((ItemHandlerBehaviour) (behaviours.getFirst())).getItemHandler();
    }

    @Override
    public IFluidHandler getMainHandler() {
        return fluidHandler;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public IEntityInstance getEntityInstance() {
        return IEntityInstance.wrap(this);
    }

    @Override
    public int getRange() {
        return 5;
    }

    @Override
    public void updateIfScheduled() {

    }

    @Override
    public void scheduleMemberUpdate() {

    }
}

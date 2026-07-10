package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.sinedkadis.terracompositio.block.IFluidApplicable;
import net.sinedkadis.terracompositio.components.FluidStackComponent;
import net.sinedkadis.terracompositio.registries.TCDataComponents;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderItem;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidApplierItem extends Item implements DispensibleContainerItem, ITCCapabilityProviderItem {
    public FluidApplierItem(Properties pProperties) {
        super(pProperties.durability(8));
    }

    public static int getRenderAmount(ItemStack stack) {
        IFluidHandlerItem item = stack.getCapability(Capabilities.FluidHandler.ITEM);
        int amount = 0;
        if (item != null) {
            FluidStack tank1 = item.getFluidInTank(0);

            amount = (int) Math.floor(tank1.getAmount()/1000f);
        }
        return amount;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Optional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(itemstack);
        if (fluidHandler.isPresent()){
            IFluidHandlerItem fluidHandlerItem = fluidHandler.get();
            FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
            Fluid fluid = fluidStack.getFluid();
            BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer,
                    !(fluid instanceof FlowingFluid) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.ANY);

            if (blockhitresult.getType() == HitResult.Type.MISS) {
                return InteractionResultHolder.pass(itemstack);
            } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
                return InteractionResultHolder.pass(itemstack);
            } else {
                BlockPos blockpos = blockhitresult.getBlockPos();
                Direction direction = blockhitresult.getDirection();
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockState = pLevel.getBlockState(blockpos);
                IFluidApplicable fluidApplicable = null;
                if (pLevel.getBlockEntity(blockpos) instanceof IFluidApplicable fluidApplicableEntity) {
                    fluidApplicable = fluidApplicableEntity;
                } else if (blockState.getBlock() instanceof IFluidApplicable fluidApplicableBlock) {
                    fluidApplicable = fluidApplicableBlock;
                }
                if (!pPlayer.isShiftKeyDown() && fluidApplicable != null) {
                    IFluidApplicable.FluidApplyResult result = fluidApplicable.tryApply(pLevel, blockpos, itemstack, fluidHandlerItem, pPlayer);
                    if (result.cancel()) return InteractionResultHolder.pass(itemstack);
                    if (result.success()) {
                        pLevel.playSound(null, blockpos, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.BLOCKS);
                        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
                    }
                }
                if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos1, direction, itemstack)) {
                    if (fluidStack.getAmount() < fluidHandlerItem.getTankCapacity(0)) {

                        if (blockState.getBlock() instanceof BucketPickup bucketpickup && !pPlayer.isShiftKeyDown()) {
                            Item item = bucketpickup.pickupBlock(pPlayer,pLevel, blockpos, blockState)
                                    .getItem();
                            if (item instanceof AirItem) return InteractionResultHolder.fail(itemstack);
                            Fluid fluid1 = ((BucketItem) item).content;
                            FluidStack resource = new FluidStack(fluid1, 1000);
                            if (fluidHandlerItem.fill(resource, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                                bucketpickup.getPickupSound(blockState).ifPresent((p_150709_) -> pPlayer.playSound(p_150709_, 1.0F, 1.0F));
                                pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, blockpos);
                                if (!pLevel.isClientSide) {
                                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) pPlayer, itemstack);
                                    fluidHandlerItem.fill(resource, IFluidHandler.FluidAction.EXECUTE);
                                }
                                return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
                            }
                        }
                        //return InteractionResultHolder.fail(itemstack);
                    }
                        BlockState blockstate = pLevel.getBlockState(blockpos);
                        BlockPos blockpos2 = canBlockContainFluid(pPlayer, pLevel, blockpos, blockstate, itemstack) ? blockpos : blockpos1;
                        if (this.emptyContents(pPlayer, pLevel, blockpos2, blockhitresult, itemstack)) {
                            this.checkExtraContent(pPlayer, pLevel, itemstack, blockpos2);
                            if (pPlayer instanceof ServerPlayer) {
                                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) pPlayer, blockpos2, itemstack);
                                fluidHandlerItem.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            }

                            pPlayer.awardStat(Stats.ITEM_USED.get(this));
                            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
                        } else {
                            return InteractionResultHolder.fail(itemstack);
                        }

                } else {
                    return InteractionResultHolder.fail(itemstack);
                }
            }
        }
        return super.use(pLevel,pPlayer,pHand);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 8-getRenderAmount(stack);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    public static BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
        float f = pPlayer.getXRot();
        float f1 = pPlayer.getYRot();
        Vec3 vec3 = pPlayer.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = pPlayer.blockInteractionRange() * 5;
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
    }

    public boolean emptyContents(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult, @Nullable ItemStack container) {
        Optional<ItemStack> itemStack = Optional.ofNullable(container);
        Optional<IFluidHandlerItem> handlerItem;
        handlerItem = itemStack
                .map(FluidUtil::getFluidHandler)
                .filter(Optional::isPresent)
                .map(Optional::get);
        Optional<FluidStack> containedFluidStack = handlerItem.map(iFluidHandlerItem -> iFluidHandlerItem.getFluidInTank(0));
        Fluid content = containedFluidStack.map(FluidStack::getFluid).orElse(Fluids.EMPTY);
        if (handlerItem.isPresent()
                && handlerItem.get()
                    .drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() != 1000) return false;
        if (!(content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = pLevel.getBlockState(pPos);
            Block block = blockstate.getBlock();
            boolean canBeReplaced = blockstate.canBeReplaced(content);
            boolean canBeFilled = blockstate.isAir() || canBeReplaced || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pPlayer,pLevel, pPos, blockstate, content);

            if (!canBeFilled) {
                return pResult != null && this.emptyContents(pPlayer, pLevel, pResult.getBlockPos().relative(pResult.getDirection()), null, container);
            } else if (containedFluidStack.isPresent() && content.getFluidType().isVaporizedOnPlacement(pLevel, pPos, containedFluidStack.get())) {
                content.getFluidType().onVaporize(pPlayer, pLevel, pPos, containedFluidStack.get());
                return true;
            } else if (pLevel.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }
                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pPlayer,pLevel,pPos,blockstate, content)) {
                ((LiquidBlockContainer)block).placeLiquid(pLevel, pPos, blockstate, ((FlowingFluid) content).getSource(false));
                handlerItem.ifPresent(iFluidHandlerItem -> this.playEmptySound(pPlayer, pLevel, pPos, iFluidHandlerItem));
                return true;
            } else {
                if (!pLevel.isClientSide && canBeReplaced && !blockstate.liquid()) {
                    pLevel.destroyBlock(pPos, true);
                }

                if (!pLevel.setBlock(pPos, content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    handlerItem.ifPresent(iFluidHandlerItem -> this.playEmptySound(pPlayer, pLevel, pPos, iFluidHandlerItem));
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, IFluidHandlerItem handlerItem) {
        Fluid content = handlerItem.getFluidInTank(0).getFluid();
        SoundEvent soundevent = content.getFluidType().getSound(pPlayer, pLevel, pPos, SoundActions.BUCKET_EMPTY);
        if (soundevent == null)
            soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
        pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Optional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(stack);
        if (fluidHandler.isPresent()) {
            FluidStack fluidStack = fluidHandler.get().getFluidInTank(0);
            tooltipComponents.add(((MutableComponent) fluidStack.getHoverName()).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.literal(fluidStack.getAmount() + "mB").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    @Override
    public boolean emptyContents(@org.jetbrains.annotations.Nullable Player pPlayer, Level pLevel, BlockPos pPos, @org.jetbrains.annotations.Nullable BlockHitResult pResult) {
        return emptyContents(pPlayer,pLevel,pPos,pResult,null);
    }

    protected boolean canBlockContainFluid(Player player, Level worldIn, BlockPos posIn, BlockState blockstate, ItemStack itemstack) {
        Optional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(itemstack);
        if (fluidHandler.isPresent()) {
            FluidStack fluidStack = fluidHandler.get().getFluidInTank(0);
            return blockstate.getBlock() instanceof LiquidBlockContainer
                    && ((LiquidBlockContainer)blockstate.getBlock())
                    .canPlaceLiquid(player,worldIn, posIn, blockstate, fluidStack.getFluid());

        }
        return false;
    }

    @Override
    public IFluidHandlerItem addFluidCapability(ItemStack itemStack) {
        return new TCFluidBucketWrapper(itemStack);
    }

    private static class TCFluidBucketWrapper extends FluidBucketWrapper {
        public TCFluidBucketWrapper(ItemStack stack) {
            super(stack);
        }

        @Override
        public FluidStack getFluid() {
            FluidStackComponent fluidStack = container.get(TCDataComponents.FLUID);
            return fluidStack == null ? FluidStack.EMPTY : fluidStack.fluidStack();
        }

        @Override
        protected void setFluid(FluidStack fluidStack) {
            container.set(TCDataComponents.FLUID,new FluidStackComponent(fluidStack));
        }

        @Override
        public int getTankCapacity(int tank) {
            return 8000;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            FluidStack current = getFluid();
            FluidStack toAdd = FluidStack.EMPTY;
            if (FluidStack.matches(resource,current) || current.isEmpty()) {
                toAdd = resource.copy();

                toAdd.setAmount(Math.min(getTankCapacity(0) - current.getAmount(),resource.getAmount()));
                if (action.execute()){
                    if (current.isEmpty()) {
                        current = toAdd.copy();
                        current.setAmount(0);
                    }
                    current.setAmount(current.getAmount() + toAdd.getAmount());
                    setFluid(current);
                }
            }
            return toAdd.getAmount();
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack current = getFluid();
            if (current.isEmpty()) return current;
            FluidStack toDrain = current.copy();
            toDrain.setAmount(Math.min(current.getAmount(),maxDrain));
            if (action.execute()){
                current.setAmount(current.getAmount() - toDrain.getAmount());
                setFluid(current);
            }
            return toDrain;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack current = getFluid();
            if (FluidStack.matches(resource,current)) {
                int maxDrain = resource.getAmount();
                FluidStack toDrain = current.copy();
                toDrain.setAmount(Math.max(current.getAmount(), maxDrain));
                if (action.execute()) {
                    current.setAmount(current.getAmount() - toDrain.getAmount());
                    setFluid(current);
                }
                return toDrain;
            }
            return FluidStack.EMPTY;
        }
    }
}

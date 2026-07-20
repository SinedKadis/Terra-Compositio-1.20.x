package net.sinedkadis.terracompositio.item.custom;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.sinedkadis.terracompositio.api.helpers.ItemHelper;
import net.sinedkadis.terracompositio.api.helpers.PlayerHelper;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.block.custom.PathPointerBlock;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCDataComponents;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.registries.TCTags;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Block.dropResources;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.sinedkadis.terracompositio.api.helpers.BlockPosHelper.getNearBlocks;
import static net.sinedkadis.terracompositio.api.helpers.BlockPosHelper.getTouchingBlocks;
import static net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties.INFUSED;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrenchAxeItem extends AxeItem {
    private static final TagKey<Block> LOGS_TAG = BlockTags.LOGS;
    public static final List<Property<?>> SURVIVAL_SAFE_PROPERTIES;


    public WrenchAxeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(TCItems.INFUSED_IRON_INGOT.get()) || pRepair.is(TCItems.WRENCH_AXE.get());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isCrouching() && !isPlayerLookingAtBlock(pPlayer,pLevel)) {
            setWrenchMode(stack,getWrenchMode(stack).next());
            pPlayer.displayClientMessage(Component.translatable("message.terracompositio.wrench_mode", getWrenchMode(stack).getDisplayName()), true);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        setWrenchMode(stack,WrenchMode.AXE);
    }

    public static WrenchMode getWrenchMode(ItemStack stack) {
        WrenchMode wrenchMode = stack.get(TCDataComponents.WRENCH_MODE);
        if (wrenchMode == null) wrenchMode = WrenchMode.AXE;
        return wrenchMode;
    }

    public static void setWrenchMode(ItemStack stack, WrenchMode mode) {
        stack.set(TCDataComponents.WRENCH_MODE,mode);
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (getWrenchMode(pPlayer.getItemInHand(InteractionHand.MAIN_HAND)) == WrenchMode.WRENCH) {
            this.wrenchInteraction(pPlayer, pState, pLevel, pPos, false, pPlayer.getItemInHand(InteractionHand.MAIN_HAND));
            return false;
        }
        if (getWrenchMode(pPlayer.getItemInHand(InteractionHand.MAIN_HAND)) == WrenchMode.CROWBAR) {
            this.crowbarLMBInteraction(pPlayer, pState, pLevel, pPos, pPlayer.getItemInHand(InteractionHand.MAIN_HAND));
            return false;
        }

        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (WrenchAxeItem.getWrenchMode(pStack).equals(WrenchMode.WRENCH)) return 255;
        if (WrenchAxeItem.getWrenchMode(pStack).equals(WrenchMode.CROWBAR)) return 255;
        return super.getDestroySpeed(pStack, pState);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return switch (getWrenchMode(stack)){
            case WRENCH_AXE,AXE -> 72000;
            default -> super.getUseDuration(stack,entity);
        };
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return switch (getWrenchMode(pStack)){
            case WRENCH_AXE -> UseAnim.BOW;
            case AXE -> UseAnim.CROSSBOW;
            default -> UseAnim.NONE;
        };
    }


    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
        if (pLivingEntity instanceof Player player) {
            if (player instanceof FakePlayer) return;
            BlockPos pos = getEntityLookingBlockPos(pLivingEntity, pLevel);
            Direction face = player.getDirection();
            if (pos == null) {
                return;
            }
            switch (getWrenchMode(pStack)) {
                case WRENCH_AXE -> this.wrenchAxeInteraction(pLevel, pos, face, player, pStack, Math.min(player.getTicksUsingItem(), 60));
                case AXE -> this.doubleAxeInteraction(pLevel, pos, face, player, pStack, Math.min(player.getTicksUsingItem(), 60));
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack stack = context.getItemInHand();
        BlockState blockState = level.getBlockState(pos);
        if (player instanceof FakePlayer) return InteractionResult.PASS;
        if (hand.equals(InteractionHand.OFF_HAND)) return InteractionResult.PASS;
        switch (getWrenchMode(stack)) {
            case WRENCH_AXE -> {
                if (player != null && !level.isClientSide && level.mayInteract(player, pos) && blockState.is(BlockTags.LOGS)) {
                    player.startUsingItem(hand);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            case WRENCH -> {
                if (!level.isClientSide && (player == null || (level.mayInteract(player, pos)))) {
                    if (this.wrenchInteraction(player, blockState, level, pos, true, stack)) {
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.FAIL;
                }
                return InteractionResult.PASS;
            }
            case AXE -> {
                if (player != null) {
                    ItemStack itemInOffHand = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (itemInOffHand.is(ItemTags.AXES)
                            && level.mayInteract(player, pos)) {
                        player.startUsingItem(InteractionHand.MAIN_HAND);
                        return InteractionResult.SUCCESS;
                    }
                }
                return super.useOn(context);
            }
            case CROWBAR -> {
                if (player != null  ) {
                    if (!this.crowbarRMBInteraction(player, level,pos,blockState,context.getClickedFace())) {
                        return InteractionResult.FAIL;
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            default -> {
                return InteractionResult.PASS;
            }
        }
    }

    private void crowbarLMBInteraction(Player pPlayer, BlockState pState, Level pLevel, BlockPos pPos, ItemStack itemInHand) {
        ItemStack wasInHand = itemInHand.copy();
        pPlayer.setItemInHand(InteractionHand.MAIN_HAND,TCItems.WRENCH_TAG_HOLDER.get().getDefaultInstance());
        BlockHitResult blockHitResult = new BlockHitResult(pPlayer.getEyePosition(), Direction.orderedByNearest(pPlayer)[0], pPos, false);

        boolean canceled = NeoForge.EVENT_BUS.post(new PlayerInteractEvent
                .RightClickBlock(pPlayer, InteractionHand.MAIN_HAND, pPos, blockHitResult)).isCanceled();
        pPlayer.setItemInHand(InteractionHand.MAIN_HAND,wasInHand);
        ItemStack itemInHandNow = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        if (canceled) {
            if (!pLevel.isClientSide())
                ItemHelper.hurtAndBreakItem((ServerLevel) pLevel,pPlayer,itemInHandNow);
            return;
        }
        if ((pState.hasBlockEntity()
                        || pState.is(TCTags.Blocks.CREATE_WRENCH_PICKUP))
                && pPlayer.isCrouching()){

            IItemHandler handler = pLevel.getCapability(Capabilities.ItemHandler.BLOCK, pPos, null);

            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (!pPlayer.addItem(handler.getStackInSlot(i))) {
                        pPlayer.drop(handler.getStackInSlot(i), true);
                    }
                }
            }

            if (!pLevel.isClientSide()) {
                LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) pLevel))
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pPos))
                        .withParameter(LootContextParams.TOOL, itemInHand);
                pState.getDrops(lootparams$builder).forEach(itemStack -> {
                    if (!pPlayer.addItem(itemStack)) {
                        pPlayer.drop(itemStack, true);
                    }
                });
                ItemHelper.hurtAndBreakItem((ServerLevel) pLevel,pPlayer,itemInHandNow);
            }
            pLevel.destroyBlock(pPos,false,pPlayer);
        }
    }

    private boolean crowbarRMBInteraction(Player player, Level level, BlockPos pos, BlockState ignoredBlockState, Direction ignoredClickedFace) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler == null) return false;

            boolean flag = false;

            for (int slot = 0; slot < handler.getSlots(); slot++) {

                ItemStack itemStack = handler.extractItem(slot, 511, false); //511 - hardcoded
                if (itemStack.isEmpty()) continue;
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, true);
                }
                flag = true;
            }
            if (flag) {
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                if (level instanceof ServerLevel serverLevel) {
                    ItemHelper.hurtAndBreakItem(serverLevel, player, player.getOffhandItem());
                }
                return true;
            }

        }
        return false;
    }

    private void doubleAxeInteraction(Level level, BlockPos pos, Direction face, Player player, ItemStack ignoredStack, int usedTicks) {
        BlockPos anchor = getAnchor(level, pos, face);
        List<BlockPos> tree = new ArrayList<>(getNearBlocks(level, anchor, LOGS_TAG, 32).stream().filter(blockPos -> blockPos.getY() >= anchor.getY()).toList());
        Collections.reverse(tree);
        List<BlockPos> peaks = tree.stream().filter(blockPos -> !level.getBlockState(blockPos.above()).is(BlockTags.LOGS)).toList();
        Set<BlockPos> allLeaves = new HashSet<>();
        peaks.forEach(blockPos -> allLeaves.addAll(getTouchingBlocks(level,blockPos,BlockTags.LEAVES,6)));
        allLeaves.forEach(blockPos -> {
            if (usedTicks > 20){
                BlockState state = level.getBlockState(blockPos);
                if (state.is(BlockTags.LEAVES)) {
                    dropResources(state, level, blockPos);
                    level.destroyBlock(blockPos, false, player, 3);
                    level.sendBlockUpdated(blockPos, state, state, Block.UPDATE_ALL);
                }
            }
        });
        int blocksToRemove = Mth.lerpInt((float) usedTicks / 60, 1, 64);
        int foodTakeCount = 6;
        for (BlockPos blockPos : tree) {
            if (usedTicks > 20 && blocksToRemove > 0) {
                BlockState state = level.getBlockState(blockPos);
                if (state.is(BlockTags.LOGS)) {
                    level.destroyBlock(blockPos, true, player, 3);
                    level.sendBlockUpdated(blockPos, state, state, Block.UPDATE_CLIENTS);
                    blocksToRemove--;
                    foodTakeCount--;
                    if (!level.isClientSide()) {
                        ItemHelper.hurtAndBreakItem((ServerLevel) level, player, player.getMainHandItem());
                        ItemHelper.hurtAndBreakItem((ServerLevel) level, player, player.getOffhandItem());
                    }

                    if (foodTakeCount == 0) {
                        foodTakeCount = 6;
                        if (!(player.getFoodData().getFoodLevel() == 0 && player.getFoodData().getSaturationLevel() == 0)) {
                            player.causeFoodExhaustion(5);
                        } else {
                            DamageSource treeChopDamageSource = level.damageSources().playerAttack(player);
                            player.hurt(treeChopDamageSource, 5.0F);
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    private void wrenchAxeInteraction(Level level, BlockPos pos, Direction face, Player player, ItemStack ignoredStack, int usedTicks) {
        if (usedTicks > 20){
            BlockPos anchor = getAnchor(level, pos, face);
            List<BlockPos> tree = getNearBlocks(level, anchor, LOGS_TAG, 64).stream().filter(blockPos -> blockPos.getY() >= anchor.getY()).toList();
            List<BlockPos> peaks = tree.stream().filter(blockPos -> !level.getBlockState(blockPos.above()).is(BlockTags.LOGS)).toList();
            Set<BlockPos> allLeaves = new HashSet<>();
            peaks.forEach(blockPos -> allLeaves.addAll(getTouchingBlocks(level, blockPos, BlockTags.LEAVES, 6)));
            allLeaves.forEach(blockPos -> {
                BlockState state = level.getBlockState(blockPos);
                if (state.is(BlockTags.LEAVES)) {
                    WorldHelper.destroyBlockSilent(level, blockPos, player);
                    level.sendBlockUpdated(blockPos, state, state, Block.UPDATE_ALL);
                }
            });
            for (BlockPos peak : peaks) {
                level.playSound(player, peak, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS);
            }
            int foodTakeCount = 6;
            Map<Integer,Integer> maxPos = new HashMap<>();
            for (BlockPos oldPos : tree) {
                BlockPos newPos = rotateTreeBlock(oldPos, face, anchor);
                BlockState oldState = level.getBlockState(oldPos);
                BlockState newState = rotateBlockState(oldState, face);
                level.setBlock(oldPos, Blocks.STRUCTURE_VOID.defaultBlockState(), 3);
                level.destroyBlock(oldPos, false, player, 512);
                level.sendBlockUpdated(oldPos, oldState, oldState, Block.UPDATE_CLIENTS);
                foodTakeCount++;
                tryToPlace(level, newPos, newState, oldPos.getY(),face, maxPos);
                if (!level.isClientSide())
                    ItemHelper.hurtAndBreakItem((ServerLevel) level, player, player.getMainHandItem());
                if (foodTakeCount == 0) {
                    foodTakeCount = 6;
                    if (!(player.getFoodData().getFoodLevel() == 0 && player.getFoodData().getSaturationLevel() == 0)) {
                        player.causeFoodExhaustion(5);
                    } else {
                        DamageSource treeChopDamageSource = level.damageSources().playerAttack(player);
                        player.hurt(treeChopDamageSource, 5.0F);
                    }
                }
            }
        }
    }

    private void tryToPlace(Level level, BlockPos pos, BlockState state, int oldY, Direction pushDirection, Map<Integer,Integer> maxPos) {
        Predicate<BlockPos> isFree = pos1 -> level.getBlockState(pos1).isAir() || level.getBlockState(pos1).getPistonPushReaction().equals(PushReaction.DESTROY);
        BlockPos.MutableBlockPos blockPos = pos.mutable().setY(oldY);

        if (!isFree.test(blockPos)) {
            maxPos.put(switch (pushDirection.getAxis()){
                case X -> blockPos.getZ();
                case Z -> blockPos.getX();
                default -> 0;
            },switch (pushDirection.getAxis()){
                case X -> blockPos.getX();
                case Z -> blockPos.getZ();
                default -> 0;
            });
            dropResources(state,level,blockPos);
            return;
        }
        BlockPos copyPos = new BlockPos(blockPos.getX(),blockPos.getY(),blockPos.getZ());
        boolean wall = switch (pushDirection.getAxis()){
            case X -> {
                if (maxPos.containsKey(blockPos.getZ()))
                    yield  !isFree.test(blockPos.setX(maxPos.get(blockPos.getZ())));
                yield false;
            }
            case Z -> {
                if (maxPos.containsKey(blockPos.getX()))
                    yield  !isFree.test(blockPos.setZ(maxPos.get(blockPos.getX())));
                yield false;
            }
            default -> true;
        };
        if (wall){
            dropResources(state,level,blockPos);
            return;
        }
        blockPos.set(copyPos);
        while (true){
            if (!isFree.test(blockPos)) {
                blockPos.move(Direction.UP);
                break;
            }
            blockPos.move(Direction.DOWN);
        }
        level.setBlockAndUpdate(blockPos, state);
        level.sendBlockUpdated(blockPos,state,state, Block.UPDATE_CLIENTS);
    }

    private BlockState rotateBlockState(BlockState state, Direction pushDirection) {
        if (state.is(TCBlocks.FLOW_CEDAR_ALTAR.get()))
            state = TCBlocks.FLOW_CEDAR_LOG.get().defaultBlockState().setValue(INFUSED,state.getValue(INFUSED));

        if (!state.hasProperty(AXIS))
            return state;

        Direction.Axis currentAxis = state.getValue(AXIS);

        Direction.Axis newAxis = switch (pushDirection) {
            case NORTH, SOUTH -> {
                if (currentAxis == Direction.Axis.Y) {
                    yield Direction.Axis.Z;
                } else if (currentAxis == Direction.Axis.Z) {
                    yield Direction.Axis.Y;
                } else {
                    yield currentAxis;
                }
            }
            case WEST, EAST -> {
                if (currentAxis == Direction.Axis.Y) {
                    yield Direction.Axis.X;
                } else if (currentAxis == Direction.Axis.X) {
                    yield Direction.Axis.Y;
                } else {
                    yield currentAxis;
                }
            }
            default -> currentAxis;
        };


        BlockState newState = state.setValue(AXIS, newAxis);
        if (state.hasProperty(INFUSED)) {
            newState = newState.setValue(INFUSED, state.getValue(INFUSED));
        }
        return newState;
    }

    private static BlockPos rotateTreeBlock(BlockPos pos, Direction direction, BlockPos anchor) {
        int x = pos.getX() - anchor.getX();
        int y = pos.getY() - anchor.getY();
        int z = pos.getZ() - anchor.getZ();


        return switch (direction) {
            case NORTH -> new BlockPos(anchor.getX() + x, anchor.getY() + z - 1, anchor.getZ() - y);
            case SOUTH -> new BlockPos(anchor.getX() + x, anchor.getY() - z - 1, anchor.getZ() + y);
            case WEST -> new BlockPos(anchor.getX() - y, anchor.getY() + x - 1, anchor.getZ() + z);
            case EAST -> new BlockPos(anchor.getX() + y, anchor.getY() - x - 1, anchor.getZ() + z);
            default -> pos;
        };
    }

    public static boolean isPlayerLookingAtBlock(Player player, Level level) {
        double reachDistance = player.blockInteractionRange();

        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookVector.scale(reachDistance));

        ClipContext context = new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult hitResult = level.clip(context);

        return hitResult.getType() == HitResult.Type.BLOCK;
    }

    @Nullable
    public static BlockPos getEntityLookingBlockPos(LivingEntity entity, Level level){
        double reachDistance;
        Vec3 eyePosition;
        Vec3 lookVector;
        Vec3 endPosition;
        if (entity instanceof Player player) {
            reachDistance = player.blockInteractionRange();
        } else {
            reachDistance = 3.0D;
        }
        eyePosition = entity.getEyePosition(1.0F);
        lookVector = entity.getLookAngle();
        endPosition = eyePosition.add(lookVector.scale(reachDistance));
        ClipContext context = new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity);
        BlockHitResult hitResult = level.clip(context);

        return hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getBlockPos() : null;
    }

    private BlockPos getAnchor(Level level, BlockPos origin, Direction face) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(origin);
        int maxDistance = 8;
        while (true) {
            if (maxDistance-- <= 0 || !level.getBlockState(mutablePos.move(face)).is(WrenchAxeItem.LOGS_TAG))
                break;
        }
        return mutablePos.immutable();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("item.terracompositio.flow_rotating_axe.mode",
                getWrenchMode(stack).getDisplayName()).withStyle(ChatFormatting.GRAY));

    }

    private boolean wrenchInteraction(@Nullable Player pPlayer, BlockState pStateClicked, LevelAccessor level, BlockPos pos, boolean rightClicked, ItemStack wrenchStack) {
        Block block = pStateClicked.getBlock();
        if (rightClicked) {
            if (block instanceof PathPointerBlock)
                return PathPointerBlockEntity.ppWrenchInteraction(pPlayer, level, pos, wrenchStack);
            if (wrenchStack.get(TCDataComponents.BIND_CORDS) != null) {
                PathPointerBlockEntity.sendBindMessage(pPlayer, "item.terracompositio.flow_rotating_axe.bind_fail_cleared");
                PathPointerBlockEntity.clearBindTags(wrenchStack);
                return false;
            }
            if (pPlayer != null && !pPlayer.isCrouching()) {
                ItemStack wasInHand = wrenchStack.copy();
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, TCItems.WRENCH_TAG_HOLDER.get().getDefaultInstance());
                BlockHitResult blockHitResult = new BlockHitResult(pPlayer.getEyePosition(), Direction.orderedByNearest(pPlayer)[0], pos, false);

                boolean canceled = NeoForge.EVENT_BUS.post(new PlayerInteractEvent
                        .RightClickBlock(pPlayer, InteractionHand.MAIN_HAND, pos, blockHitResult)).isCanceled();
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, wasInHand);
                if (canceled) {
                    return false;
                }
            }
        }

        StateDefinition<Block, BlockState> blockStateDefinition = block.getStateDefinition();
        Collection<Property<?>> properties = blockStateDefinition.getProperties().stream().filter(SURVIVAL_SAFE_PROPERTIES::contains).toList();
        if (properties.isEmpty()) {
            if (pPlayer != null)
                PlayerHelper.message(pPlayer, Component.translatable("item.terracompositio.flow_rotating_axe.no_change").withStyle(ChatFormatting.BOLD));
            return false;
        } else {
            String debugPropertyName = wrenchStack.get(TCDataComponents.CHANGE_PROPERTY);
            if (debugPropertyName == null) debugPropertyName = "";
            Property<?> blockStateDefinitionProperty = blockStateDefinition.getProperty(debugPropertyName);
            if (rightClicked) {
                if (blockStateDefinitionProperty == null) {
                    blockStateDefinitionProperty = properties.iterator().next();
                }
                BlockState newState;
                if (pPlayer != null) {
                    newState = cycleState(pStateClicked, blockStateDefinitionProperty, pPlayer.isSecondaryUseActive());
                    ItemHelper.hurtAndBreakItem((ServerLevel) level,pPlayer,wrenchStack);
                } else {
                    newState = cycleState(pStateClicked, blockStateDefinitionProperty, false);
                    wrenchStack.getItem().damageItem(wrenchStack,1,null,(item) -> {
                        wrenchStack.shrink(1);
                        wrenchStack.setDamageValue(0);
                    });
                }
                level.getChunkSource().getLightEngine().checkBlock(pos);
                ((Level) level).getChunkAt(pos).setBlockState(pos, newState, false);
                PlayerHelper.message(pPlayer, Component.translatable(Items.DEBUG_STICK.getDescriptionId() + ".update", blockStateDefinitionProperty.getName(), getNameHelper(newState, blockStateDefinitionProperty)).withStyle(ChatFormatting.BOLD));
            } else {
                if (pPlayer != null) {
                    blockStateDefinitionProperty = getRelative(properties, blockStateDefinitionProperty, pPlayer.isSecondaryUseActive());
                } else {
                    blockStateDefinitionProperty = getRelative(properties, blockStateDefinitionProperty, false);
                }
                String newProperty = blockStateDefinitionProperty.getName();
                wrenchStack.set(TCDataComponents.CHANGE_PROPERTY, newProperty);
                if (pPlayer != null)
                    PlayerHelper.message(pPlayer, Component.translatable(Items.DEBUG_STICK.getDescriptionId() + ".select", newProperty, getNameHelper(pStateClicked, blockStateDefinitionProperty)).withStyle(ChatFormatting.BOLD));
            }
            return true;
        }
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState pState, Property<T> pProperty, boolean pBackwards) {
        return pState.setValue(pProperty, getRelative(pProperty.getPossibleValues(), pState.getValue(pProperty), pBackwards));
    }
    private static <T> T getRelative(Iterable<T> pAllowedValues, @javax.annotation.Nullable T pCurrentValue, boolean pBackwards) {
        return pBackwards ? Util.findPreviousInIterable(pAllowedValues, pCurrentValue) : Util.findNextInIterable(pAllowedValues, pCurrentValue);
    }

    private static <T extends Comparable<T>> String getNameHelper(BlockState pState, Property<T> pProperty) {
        return pProperty.getName(pState.getValue(pProperty));
    }

    public static void onRenderHandEvent(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();

        if (mainHandStack.getItem() instanceof WrenchAxeItem && offHandStack.getItem() instanceof AxeItem && player.getUseItem().is(TCItems.WRENCH_AXE.get())) {
            event.setCanceled(true);
            renderAxes(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getHand());
        }
    }

    private static void renderAxes(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, InteractionHand hand) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return;
        int duration = Math.min(player.getTicksUsingItem(), 60);

        ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
        if (hand == InteractionHand.MAIN_HAND) {
            poseStack.pushPose();
            poseStack.translate(0.35F, -0.1F, -0.3F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.mulPose(Axis.XN.rotationDegrees(90 + duration * 1.5F));


            itemInHandRenderer.renderItem(player, stack, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                    true, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.translate(-0.35F, -0.1F, -0.3F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.mulPose(Axis.XN.rotationDegrees(90 + duration * 1.5F));

            itemInHandRenderer.renderItem(player, stack, ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    false, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }

    public static void onClientLevelTickEndEvent(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        Level level = player.level();

        if (!player.getMainHandItem().is(TCItems.WRENCH_AXE.get())) return;

        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        BlockPos pos = blockHit.getBlockPos();
        if (pos.distSqr(player.blockPosition()) > 25) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PathPointerBlockEntity pointer) {
            pointer.highlightNodes();
        }
    }

    static {
        SURVIVAL_SAFE_PROPERTIES = List.of(
                AXIS,
                FACING,
                FACING_HOPPER,
                HORIZONTAL_AXIS,
                HORIZONTAL_FACING,
                IN_WALL,
                POWERED,
                ROTATION_16,
                RAIL_SHAPE,
                RAIL_SHAPE_STRAIGHT,
                HALF,
                NOTEBLOCK_INSTRUMENT,
                VERTICAL_DIRECTION,
                WEST,
                WEST_REDSTONE,
                WEST_WALL,
                NORTH,
                NORTH_REDSTONE,
                NORTH_WALL,
                EAST,
                EAST_REDSTONE,
                EAST_WALL,
                SOUTH,
                SOUTH_REDSTONE,
                SOUTH_WALL
        );
    }

    public enum WrenchMode {
        AXE,
        WRENCH_AXE,
        WRENCH,
        CROWBAR;

        public WrenchMode next() {
            // Переход к следующему режиму по кругу
            return values()[(this.ordinal() + 1) % values().length];
        }

        public Component getDisplayName() {
            return Component.translatable("item.terracompositio.flow_rotating_axe." + name().toLowerCase());
        }

        public static WrenchMode fromOrdinal(int ordinal) {
            return values()[ordinal % values().length];
        }


        public static final Codec<WrenchMode> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("wrench_mode")
                                .forGetter(Enum::ordinal)
                ).apply(instance,WrenchMode::fromOrdinal));

        public static final StreamCodec<RegistryFriendlyByteBuf, WrenchMode> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public WrenchMode decode(RegistryFriendlyByteBuf buffer) {
                return WrenchAxeItem.WrenchMode.fromOrdinal(buffer.readVarInt());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, WrenchMode value) {
                buffer.writeVarInt(value.ordinal());
            }
        };
    }
}


package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.IECFStorageExtensionItem;
import net.sinedkadis.terracompositio.api.IHaveExtensibleECFStorageItem;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.helpers.BlockPosHelper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.api.registries.TCBlockStateProperties;
import net.sinedkadis.terracompositio.registries.*;
import net.sinedkadis.terracompositio.config.TCClientConfigs;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.ecf.ECFItemWrapper;
import net.sinedkadis.terracompositio.network.payloads.C2SBoardSyncPayload;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderItem;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class TechnetiumArmorItem extends TCArmorItem implements IHaveExtensibleECFStorageItem, ITCCapabilityProviderItem {

    private static final String last = "last";
    private static final String height = "Height";
    private static final String cd = "cd";

    @Override
    public Type getType() {
        return type;
    }

    private final Type type;

    public TechnetiumArmorItem(Type pType, Properties pProperties) {
        super(TCArmorMaterials.TECHNETIUM, pType, pProperties);
        this.type = pType;
    }


    public static void onLivingHurtEvent(EntityInvulnerabilityCheckEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        DamageSource source = event.getSource();
        Entity damager = source.getEntity();
        if (damager == null) return;

        ItemStack itemBySlot = player.getItemBySlot(EquipmentSlot.CHEST);
        if (itemBySlot.is(TCItems.TECHNETIUM_CHESTPLATE.get())) {
            IECFHandler iECFHandler = itemBySlot.getCapability(TCCapabilities.ECF_HANDLER_ITEM);
            if (iECFHandler == null) iECFHandler = SentinelHelper.EMPTY_ECF_HANDLER;
            if (iECFHandler.takeECF(1, true) > 0) {
                Level level = player.level();
                BlockPos pPos = player.blockPosition();
                if (player.getRandom().nextFloat() > 0.3f) {
                    iECFHandler.takeECF(1, false);
                    if (iECFHandler.getECF() <= 0) {
                        level.playSound(null,
                                pPos,
                                SoundEvents.SHIELD_BREAK,
                                SoundSource.PLAYERS);
                    }
                }


                level.playSound(null,
                        pPos,
                        SoundEvents.SHIELD_BLOCK,
                        SoundSource.PLAYERS);

                ParticleHelperInternal.spawnParticlesIn(level, pPos.above());
                event.setInvulnerable(true);
            }
        }
    }

    public static void onBlockChanged(LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player))
            return;
        if (player.mayFly()) return;

        Level level = livingEntity.level();
        if (!level.isClientSide()) return;

        BlockPos onPos = BlockPos.containing(player.position().add(0,-1,0));
        BlockPos standingPos = player.getOnPos();

        BlockState standingState = level.getBlockState(standingPos);

        if (standingState.is(Blocks.AIR)) {
            standingState = level.getBlockState(standingPos.below());
        }

        CompoundTag persistentData = player.getPersistentData();

        IECFHandler iECFHandler = player.getItemBySlot(EquipmentSlot.FEET).getCapability(TCCapabilities.ECF_HANDLER_ITEM);

        if (iECFHandler == null) return;

        boolean fallSaveActivated = setHeightIfFalling(level, player, iECFHandler, onPos, persistentData);

        boolean standingOnBoard = standingState.is(TCBlocks.ECF_BOARD.get()) && !standingState.getValue(TCBlockStateProperties.PERMANENT);

        boolean jumped = justJumped(player, persistentData);

        if (!standingOnBoard && !fallSaveActivated && !jumped
                && persistentData.contains(height)) {
            persistentData.remove(height);
        }

        String last = TechnetiumArmorItem.last;
        BlockPos destroyPos = null;
        if (persistentData.contains(last)) {
            if (level.isClientSide()) {
                destroyPos = BlockPosHelper.loadBlockPos(persistentData.getCompound(last));
            }
        }

        if (!persistentData.contains(height)) {
            if (destroyPos != null) {
                level.destroyBlock(destroyPos, true);
                PacketDistributor.sendToServer(new C2SBoardSyncPayload(destroyPos, false, 0, false));
                persistentData.remove(last);
            }
            return;
        }


        BlockPos posOnHeight = onPos.atY(persistentData.getInt(height));
        BlockState blockStateOnHeight = level.getBlockState(posOnHeight);

        boolean allowBoardPlace = blockStateOnHeight.is(BlockTags.REPLACEABLE)
                && !blockStateOnHeight.is(TCBlocks.ECF_BOARD.get())
                && (standingState.is(TCBlocks.ECF_BOARD.get()) || fallSaveActivated || jumped);

        if (destroyPos != null && (allowBoardPlace || !posOnHeight.equals(destroyPos))) {
            level.destroyBlock(destroyPos, true);
            PacketDistributor.sendToServer(new C2SBoardSyncPayload(destroyPos, false, 0, false));
            persistentData.remove(last);
        }

        if (allowBoardPlace && !livingEntity.isShiftKeyDown()) {
            FluidState fluidState = blockStateOnHeight.getFluidState();
            boolean waterlogged = (blockStateOnHeight.hasProperty(WATERLOGGED) && blockStateOnHeight.getValue(WATERLOGGED))
                    || fluidState.is(Fluids.WATER);

            BlockState boardState = TCBlocks.ECF_BOARD.get().defaultBlockState().setValue(WATERLOGGED, waterlogged);

            takeECFAndSetBoard(iECFHandler, level, posOnHeight, boardState);
            persistentData.put(last, BlockPosHelper.saveBlockPos(posOnHeight));
        }
    }

    public static boolean setHeightIfFalling(Level level, Entity entity, IECFHandler IECFHandler, BlockPos onPos, CompoundTag persistentData) {
        float fallDistance = entity.fallDistance;
        if (fallDistance > 3 && !entity.isShiftKeyDown()
                && IECFHandler.getECF() >= 1) {

            BlockState blockStateBelow1 = level.getBlockState(onPos.below());
            BlockState blockStateBelow3 = level.getBlockState(onPos.below(3));

            if (blockStateBelow1.is(BlockTags.REPLACEABLE)
                    && !blockStateBelow3.isAir()) {
                persistentData.putInt(height, onPos.getY() - 1);
                return true;
            }
        }
        return false;
    }

    public static boolean justJumped(Entity entity, CompoundTag persistentData) {
        return persistentData.contains(cd) && (entity.tickCount - persistentData.getInt(cd) < 30);
    }

    public static void onDoubleJump(LocalPlayer localPlayer) {
        if (localPlayer.mayFly()) return;
        ItemStack itemBySlot = localPlayer.getItemBySlot(EquipmentSlot.FEET);
        if (!itemBySlot.is(TCItems.TECHNETIUM_BOOTS.get())) return;

        IECFHandler iECFHandler = itemBySlot.getCapability(TCCapabilities.ECF_HANDLER_ITEM);
        if (iECFHandler == null || !(iECFHandler.takeECF(1, false) > 0)) return;

        CompoundTag persistentData = localPlayer.getPersistentData();

        persistentData.putInt(cd, localPlayer.tickCount);
        localPlayer.level().playSound(localPlayer, localPlayer.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS);
        localPlayer.move(MoverType.SELF, new Vec3(0, 6, 0));
        persistentData.putInt(height, localPlayer.getBlockY() - 2);

    }

    private static void takeECFAndSetBoard(IECFHandler IECFHandler, Level level, BlockPos posOnHeight, BlockState boardState) {
        if (IECFHandler.takeECF(1, false) > 0 && level.isClientSide()) {
            level.destroyBlock(posOnHeight,true);
            level.setBlock(posOnHeight, boardState, 1);
            PacketDistributor.sendToServer(new C2SBoardSyncPayload(
                    posOnHeight,
                    true,
                    1,
                    boardState.getValue(WATERLOGGED))
            );
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player player)) return;
        if (player.mayFly()) return;

        Level level = livingEntity.level();
        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

        if (!stack.is(TCItems.TECHNETIUM_BOOTS.get())) return;

        CompoundTag persistentData = livingEntity.getPersistentData();

        BlockPos onPos = BlockPos.containing(
                livingEntity.position().add(0, -1, 0)
        );
        BlockState blockStateOn = level.getBlockState(onPos);

        IECFHandler iECFHandler = stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM);
        if (iECFHandler == null || iECFHandler.getECF() < 1) return;

        if (!blockStateOn.is(TCBlocks.ECF_BOARD.get())) {
            calculateVelocityAndSetBoard(onPos, livingEntity, level, persistentData);
        } else if (!blockStateOn.getValue(TCBlockStateProperties.PERMANENT)) {
            changeHeightByView(livingEntity, persistentData, level);
        }
        persistentData.putInt(cd, livingEntity.tickCount);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity entity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, entity, pSlotId, pIsSelected);
        IECFHandler iECFHandler = pStack.getCapability(TCCapabilities.ECF_HANDLER_ITEM);
        if (iECFHandler == null) return;
        switch (type) {
            case HELMET -> this.helmetInventoryTick(pStack, pLevel, entity, iECFHandler);
            case CHESTPLATE -> {
                // made via onLivingHurt
            }
            case LEGGINGS -> this.leggingsInventoryTick(pStack, pLevel, entity, iECFHandler);
            case BOOTS -> {
                // made via onBlockChanged and onLivingJump
            }

            default -> {
            }

        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        boolean canEquip = super.canEquip(stack, armorType, entity);
        if (entity instanceof ECFNetworkMember member) {
            if (canEquip) {
                if (armorType.equals(EquipmentSlot.HEAD)) {
                    TerraCompositioAPI.INSTANCE.getECFNetworkInstance().fireECFNetworkEvent(member, NetworkAction.UPDATE);
                }
            }
        }

        return canEquip;
    }

    private void helmetInventoryTick(ItemStack ignoredPStack, Level ignoredPLevel, Entity pEntity, IECFHandler ignoredIECFHandler) {
        if (pEntity.tickCount % 20 != 0) return;
        IECFHandler playerHandler = pEntity.getCapability(TCCapabilities.ECF_HANDLER_ENTITY);

        if (playerHandler == null) return;

        if (playerHandler.getFreeSpace() > 0) {
            TerraCompositioAPI.instance().getECFNetworkInstance().fireECFNetworkEvent((ECFNetworkMember) pEntity, NetworkAction.UPDATE);
        }
    }

    private void leggingsInventoryTick(ItemStack itemStack, Level ignoredPLevel, Entity entity, IECFHandler thisHandler) {
        for (ItemStack stack : ((LivingEntity) entity).getArmorSlots()) {
            if (stack.equals(itemStack)) {
                IECFHandler playerHandler = Objects.requireNonNull(entity.getCapability(TCCapabilities.ECF_HANDLER_ENTITY))
                        .getMainHandler();
                int taken = thisHandler.addECF(TCCommonConfigs.ECF_PER_BURST_TRANSFER_LIMIT.get(), true);
                int added = playerHandler.takeECF(taken, false);
                thisHandler.addECF(added, false);
                continue;
            }
            IECFHandler iecfHandler = stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM);
            if (iecfHandler == null) return;
            int taken = thisHandler.takeECF(TCCommonConfigs.ECF_PER_BURST_TRANSFER_LIMIT.get(), true);
            int added = iecfHandler.addECF(taken, false);
            thisHandler.takeECF(added, false);
        }
    }

    private static void changeHeightByView(LivingEntity livingEntity,
                                           CompoundTag persistentData,
                                           Level level) {
        if (!livingEntity.isShiftKeyDown() && level.isClientSide()) {
            int h = persistentData.getInt(height);

            float viewXRot = -livingEntity.getXRot();

            if (viewXRot > 30) {
                h++;
            } else if (viewXRot < -30) {
                h--;
            } else return;

            persistentData.putInt(height, h);
        }
    }

    private static void calculateVelocityAndSetBoard(BlockPos onPos,
                                                     LivingEntity livingEntity,
                                                     Level level,
                                                     CompoundTag persistentData) {

        Vec3 deltaMovement = livingEntity.getDeltaMovement();

        double deltaX = Math.round(deltaMovement.x() * 10);
        double deltaZ = Math.round(deltaMovement.z() * 10);
        BlockPos relative = onPos.offset((int) deltaX, 0, (int) deltaZ);
        if (level.getBlockState(relative).is(BlockTags.REPLACEABLE)) {
            persistentData.putInt(height, onPos.getY());
        }
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack,
                                                      Entity entity,
                                                      EquipmentSlot slot,
                                                      ArmorMaterial.Layer layer,
                                                      boolean innerModel) {
        return switch (slot) {
            case HEAD -> TerraCompositio.modLoc("textures/models/armor/technetium_crown.png");
            case CHEST -> {
                if (Objects.requireNonNull(stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM)).getECF() <= 0)
                    yield TerraCompositio.modLoc("textures/models/armor/technetium_chestplate/armor_layer_no_shield.png");
                int textureIndex = (int) (Util.getMillis() / 300) % 16;
                yield TerraCompositio.modLoc("textures/models/armor/technetium_chestplate/armor_layer_"
                        + textureIndex + ".png");
            }
            case FEET -> TerraCompositio.modLoc("textures/models/armor/technetium_boots.png");
            case LEGS -> TerraCompositio.modLoc("textures/models/armor/technetium_leggings.png");
            default -> null;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null
                && player.getData(TCAttachments.KNOWLEDGE).isCreationAcknowledged()
                && TCClientConfigs.APPLE_ITEM_TOOLTIP.get()) {
            tooltipComponents.add(
                    TooltipHelper.keyWithArg(TooltipHelper.Keys.ECF,
                            Objects.requireNonNull(stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM)).getECF())
            );
            IECFStorageExtensionItem currentExtension = this.getCurrentExtension(stack);
            if (currentExtension.maxStorage() > 0) {
                Component description = currentExtension.self().getItem().getDescription();
                tooltipComponents.add(
                        TooltipHelper.keyWithArg(TooltipHelper.Keys.STORAGE_EXTENSION, description, TooltipHelper.Units.NO_UNITS)
                );
            }
            if (TCCommonConfigs.DEBUG.get()) {
                tooltipComponents.add(
                        TooltipHelper.keyWithArg(TooltipHelper.Keys.MAX_ECF,
                                Objects.requireNonNull(stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM)).getMaxECF())
                );
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }





    @Override
    public IECFHandler addECFCapability(ItemStack itemStack) {
        return new ECFItemWrapper(itemStack);
    }

    @Override
    public IECFStorageExtensionItem getCurrentExtension(ItemStack stack) {
        ItemStack stack1 = stack.get(TCDataComponents.ECF_STORAGE_EXTENSION);
        if (stack1 == null) return () -> 0;

        Item item = stack1.getItem();
        return item instanceof IECFStorageExtensionItem iecfse ? iecfse : () -> 0;
    }

    @Override
    public void setExtension(ItemStack stack, IECFStorageExtensionItem extensionItem) {
        if (extensionItem.maxStorage() <= 0) return;
        stack.set(TCDataComponents.ECF_STORAGE_EXTENSION,extensionItem.self());
        Objects.requireNonNull(stack.getCapability(TCCapabilities.ECF_HANDLER_ITEM)).setMaxECF(extensionItem.maxStorage());
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return this.getCurrentExtension(itemStack).self();
    }
}

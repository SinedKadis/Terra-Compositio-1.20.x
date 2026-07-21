package net.sinedkadis.terracompositio.entity.custom;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.dummies.DummyECFHandler;
import net.sinedkadis.terracompositio.api.helpers.ECFHelper;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.config.TCClientConfigs;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.ecf.DefaultECFHandler;
import net.sinedkadis.terracompositio.ecf.ECFNetworkHandler;
import net.sinedkadis.terracompositio.ecf.PPECFMemberProxy;
import net.sinedkadis.terracompositio.entity.goals.ECFExtractGoal;
import net.sinedkadis.terracompositio.entity.goals.ECFHoldGoal;
import net.sinedkadis.terracompositio.entity.goals.ReachSourceGoal;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderInstance;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// /kill @e[type=terracompositio:flow_cedar_ent_entity]

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlowCedarEntEntity extends AbstractGolem implements ECFNetworkMember, IHaveKnowledge, ITCCapabilityProviderInstance {
    private static final EntityDataAccessor<Boolean> EXTRACTING =
            SynchedEntityData.defineId(FlowCedarEntEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOLDING =
            SynchedEntityData.defineId(FlowCedarEntEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ECF_DATA =
            SynchedEntityData.defineId(FlowCedarEntEntity.class, EntityDataSerializers.INT);
    public final AnimationState ecfHoldState = new AnimationState();
    protected IECFHandler holdECFHandler = new DefaultECFHandler(this)
            .setMaxECF(64000)
            .setOffset(vec3 -> vec3.add(0, this.getBbHeight() + (0.1f + (this.getSyncedECF() / 10000d)) * 10 * 0.2f, 0))
            .setIndex(0);
    @Getter
    protected IECFHandler innerECFHandler = new DefaultECFHandler(this)
            .setMaxECF(32)
            .setOffset(vec3 -> vec3.add(0,1,0))
            .setIndex(1);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState extractionAnimationState = new AnimationState();
    public final AnimationState extractionCompleteAnimationState = new AnimationState();
    @Getter
    public final List<IECFHandler> ecfHandlers = List.of(holdECFHandler, innerECFHandler);

    protected int scheduledMembersUpdate = -1;
    protected Set<PPECFMemberProxy> scheduledMembers = new HashSet<>();

    boolean scheduledUpdate = false;

    public FlowCedarEntEntity(EntityType<? extends AbstractGolem> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canTakeItem(ItemStack pItemstack) {
        return pItemstack.is(TCItems.TECHNETIUM_CROWN.get());
    }

    @Override
    public IECFHandler getECFCapability(@Nullable Direction direction) {
        return holdECFHandler;
    }

    private int lastSyncedEnergy = -1;

    public static boolean checkEntSpawnRules(
            EntityType<FlowCedarEntEntity> bat, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        if (pos.getY() >= 63) {
            return false;
        } else {
            int i = level.getMaxLocalRawBrightness(pos);
            int j = 4;
            if (random.nextBoolean()) {
                return false;
            }

            return i <= random.nextInt(j) && checkMobSpawnRules(bat, level, spawnType, pos, random);
        }
    }

    public static void onEntityInteractEvent(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof FlowCedarEntEntity entity) {
            ItemStack pStack = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (pStack.is(TCItems.TECHNETIUM_CROWN.get())) {
                if (head.isEmpty()) {
                    entity.setItemSlot(EquipmentSlot.HEAD, pStack.copy());
                    pStack.shrink(1);
                    entity.setDropChance(EquipmentSlot.HEAD, 2.0F);
                    entity.setPersistenceRequired();
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    ECFNetworkHandler.INSTANCE.fireECFNetworkEvent(entity, NetworkAction.UPDATE);
                    return;
                }
            }
            if (pStack.isEmpty()) {
                if (!head.isEmpty()) {
                    event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, head.copy());
                    head.shrink(1);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateIfScheduled();
        ItemStack item = this.getItemBySlot(EquipmentSlot.HEAD);
        item.getItem().inventoryTick(item,level(),this,3,false);
        if (this.level().isClientSide()) {
            setupAnimationStates();
        } else {
            // Серверная логика
            ECFNetwork ECFNetworkInstance = TerraCompositioAPI.instance().getECFNetworkInstance();
            boolean inNetwork = ECFNetworkInstance.isIn(this.level(), this);
            if (!inNetwork && !this.isRemoved()) {
                ECFNetworkInstance.fireECFNetworkEvent(this, NetworkAction.ADD);
            }


                int currentEnergy = holdECFHandler.getECF();

                if (currentEnergy > 10000) abortECFConsume();

                if (currentEnergy != lastSyncedEnergy) {
                    setSyncedECF(currentEnergy);
                    lastSyncedEnergy = currentEnergy;
                }


                    if (tickCount % 20 == 0) {
                        int cfe = TCCommonConfigs.ECF_PER_BURST_TRANSFER_LIMIT.get();
                        int taken = holdECFHandler.takeECF(cfe, TransferAction.SIMULATE);
                        int added = innerECFHandler.addECF(
                                taken,
                                TransferAction.SIMULATE
                        );

                        if (added > 0) {
                            holdECFHandler.takeECF(added, TransferAction.EXECUTE);
                            innerECFHandler.addECF(
                                    added,
                                    TransferAction.EXECUTE
                            );
                        }
                        if (taken > 0) {
                            if (this.level() instanceof ServerLevel serverLevel)
                                ParticleHelperInternal.sendECFParticles(
                                        serverLevel,
                                        innerECFHandler.getOffset().apply(position()),
                                        holdECFHandler.getOffset().apply(position()),
                                        (int) Math.floor(cfe * TCClientConfigs.ECF_RENDER_MULTIPLIER.get()));
                        }
                    }
                    if (tickCount % 200 == 0) {
                        innerECFHandler.takeECF(1, TransferAction.EXECUTE);
                        if (innerECFHandler.getECF() <= 0) {
                            this.turnIntoStatue();
                        }
                    }


        }
    }

    public void turnIntoStatue() {
        ItemStack crown = this.getItemBySlot(EquipmentSlot.HEAD);
        this.level().setBlock(this.blockPosition(),
                TCBlocks.FLOW_CEDAR_ENT_STATUE.get().defaultBlockState(),3);

        IItemHandler capability = level().getCapability(Capabilities.ItemHandler.BLOCK, blockPosition(), null);
        if (capability != null) {
            if (capability instanceof ItemStackHandler itemStackHandler)
                itemStackHandler.setStackInSlot(0,crown);
        }

        this.remove(RemovalReason.CHANGED_DIMENSION);
    }


    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        int max;
        if (spawnType.equals(MobSpawnType.SPAWN_EGG))
            max = 5;
        else
            max = level().getRandom().nextInt(6, 12);
        this.innerECFHandler.setECF(max);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EXTRACTING, false);
        builder.define(HOLDING, false);
        builder.define(ECF_DATA, 0);
    }

    public void setExtracting(boolean extracting) {
        this.entityData.set(EXTRACTING, extracting);
    }

    public boolean isExtracting() {
        return this.entityData.get(EXTRACTING);
    }

    public void setHolding(boolean extracting) {
        this.entityData.set(HOLDING, extracting);
    }

    public boolean isHolding() {
        return this.entityData.get(HOLDING);
    }

    public int getSyncedECF() {
        return this.entityData.get(ECF_DATA);
    }

    public void setSyncedECF(int amount) {
        if (!this.level().isClientSide()) {
            this.entityData.set(ECF_DATA, amount);
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        TerraCompositioAPI.INSTANCE.getECFNetworkInstance().fireECFNetworkEvent(this, NetworkAction.REMOVE);
    }

    boolean wasHeld = false;
    private void setupAnimationStates() {
        idleAnimationState.startIfStopped(this.tickCount);

        extractionAnimationState.animateWhen(this.isExtracting(),this.tickCount);
        ecfHoldState.animateWhen(this.isHolding(), this.tickCount);
        if (!isHolding() && wasHeld){
            ecfHoldState.stop();
            extractionCompleteAnimationState.start(this.tickCount);
        }
        wasHeld = isHolding();

    }
    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new ECFHoldGoal(this));
//        this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
//        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.COOKED_BEEF), false));

        this.goalSelector.addGoal(3, new ReachSourceGoal(this, 1.2D, 32, 3));
        this.goalSelector.addGoal(3, new ECFExtractGoal(this, 4));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

    }



    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 2f);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.WOOD_FALL;
    }


    @Override
    public IEntityInstance getEntityInstance() {
        return IEntityInstance.wrap(this);
    }

    @Override
    public int getRange(boolean inner) {
        if (inner) return 1;
        return 5;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public IECFHandler getECFHandler() {
        return holdECFHandler;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        holdECFHandler.writeToNBT(level().registryAccess(), pCompound);
        innerECFHandler.writeToNBT(level().registryAccess(), pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        holdECFHandler.readFromNBT(level().registryAccess(), pCompound);
        innerECFHandler.readFromNBT(level().registryAccess(), pCompound);
    }


    public void abortECFConsume() {
        if (!this.level().isClientSide()){
            float scale = (0.1f + (holdECFHandler.getECF() / (float) holdECFHandler.getMaxECF())) * 10;
            ParticleHelperInternal.spawnParticlesIn(this.level(),
                    BlockPos.containing(this.position().add(0, this.getBbHeight() + scale * 0.2f, 0)),
                    holdECFHandler.getECF() / 10);
            holdECFHandler.setECF(0);
        }
    }

    public void sendViaPP(PPECFMemberProxy current) {
        if (getECFHandler().getECF() > 0 && TerraCompositioAPI.instance().getECFNetworkInstance().validateMember(current)) {
            if (current.getECFHandler().getFreeSpace() > TCCommonConfigs.ECF_PER_BURST_TRANSFER_LIMIT.get())
                scheduleMemberUpdate(current);
            ECFHelper.newTransfer().targetAndSource(current, this).speed(5 / 20f).build();
        }
    }

    @Override
    public void scheduleMemberUpdate(ECFNetworkMember updated) {
        if (updated instanceof PPECFMemberProxy proxy) {
            this.scheduledMembers.add(proxy);
            if (scheduledMembersUpdate < 0) scheduledMembersUpdate = TCCommonConfigs.TICKS_BETWEEN_BURSTS.get();
        }
    }

    @Override
    public void scheduleMemberUpdate() {
        scheduledUpdate = true;
    }

    @Override
    public void updateIfScheduled() {
        if (scheduledUpdate) {
            scheduledUpdate = false;
            onECFNetworkMemberUpdate();
        }
        if (scheduledMembersUpdate == 0) {
            scheduledMembersUpdate = -1;
            Set<PPECFMemberProxy> scheduledMembers1 = Set.copyOf(this.scheduledMembers);
            this.scheduledMembers.clear();
            scheduledMembers1.forEach(this::sendViaPP);
        } else if (scheduledMembersUpdate > 0)
            scheduledMembersUpdate--;
    }

    @Override
    public void onECFNetworkMemberUpdate() {
        if (holdECFHandler.getECF() > 0) {
            Set<ECFNetworkMember> allECFNetworkMembers = TerraCompositioAPI.instance().getECFNetworkInstance().getAllECFNetworkMembers(level());
            allECFNetworkMembers.stream()
                    .filter(PathPointerBlockEntity.class::isInstance)
                    .filter(member -> member.getEntityInstance().tc$getBlockPos().closerThan(blockPosition(), getRange()))
                    .map(PathPointerBlockEntity.class::cast)
                    .filter(member -> member.parts.contains(PathPointerBlockEntity.PPPart.EXTRACTOR))
                    .map(member -> {
                        Optional<ECFNetworkMember> availableNetworkTargets = TerraCompositioAPI.instance().getECFNetworkInstance()
                                .getAvailableNetworkTargets(this).stream().findAny();

                        if (availableNetworkTargets.isPresent()) {
                            return new PPECFMemberProxy(
                                    availableNetworkTargets.get(),
                                    member
                            );
                        }
                        return SentinelHelper.EMPTY_ECF_HANDLER;

                    })
                    .filter(member -> !(member instanceof DummyECFHandler))
                    .map(PPECFMemberProxy.class::cast)
                    .findAny()
                    .ifPresent(this::sendViaPP);
        }
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {



            data.putInt(TooltipHelper.Keys.ECF.toData(), holdECFHandler.getECF());
        data.putInt(TooltipHelper.Keys.MAX_ECF.toData(), holdECFHandler.getMaxECF());
        if (TCCommonConfigs.DEBUG.get()) {
                data.putInt(TooltipHelper.Keys.QUEUED.toData(), holdECFHandler.getQueued());
            }


            data.putInt(TooltipHelper.Keys.ECF.toData() + 2, innerECFHandler.getECF());
        data.putInt(TooltipHelper.Keys.MAX_ECF.toData() + 2, innerECFHandler.getMaxECF());
            if (TCCommonConfigs.DEBUG.get()) {
                data.putInt(TooltipHelper.Keys.QUEUED.toData() + 2, innerECFHandler.getQueued());
            }


        int priority = this.getPriority();

        if (TCCommonConfigs.DEBUG.get()) {
            data.putInt(TooltipHelper.Keys.PRIORITY.toData(), priority);
        }
        data.putInt(TooltipHelper.Keys.RANGE.toData(), this.getRange());

    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {

        TooltipHelper.addWithHeader(TooltipHelper.Headers.ECF, tooltip, t1 -> {
            TooltipHelper.addWithHeader(TooltipHelper.Headers.ENT_HOLD, t1, t -> {
                if (isShifting) {
                    TooltipHelper.addIfExist(TooltipHelper.Keys.ECF, t, data);
                    TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_ECF, t, data);
                    TooltipHelper.addIfExist(TooltipHelper.Keys.QUEUED, t, data);
                } else {
                    TooltipHelper.addScaleIfExist(TooltipHelper.Keys.ECF, TooltipHelper.Keys.MAX_ECF, tooltip, data);
                }
            });


            TooltipHelper.addWithHeader(TooltipHelper.Headers.ENT_INNER, t1, t -> {
                if (isShifting) {
                    TooltipHelper.addIfExist(TooltipHelper.Keys.ECF, t, data, 2);
                    TooltipHelper.addIfExist(TooltipHelper.Keys.MAX_ECF, t, data, 2);
                    TooltipHelper.addIfExist(TooltipHelper.Keys.QUEUED, t, data, 2);
                    t.add(TooltipHelper.keyWithArg(TooltipHelper.Keys.CONSUME, 0.1, TooltipHelper.Units.ECF_SECOND));
                } else {
                    TooltipHelper.addScaleIfExist(TooltipHelper.Keys.ECF, TooltipHelper.Keys.MAX_ECF, tooltip, data, 2);
                }
            });

            TooltipHelper.addWithHeader(TooltipHelper.Headers.ENT_COMMON, t1, t -> {
                TooltipHelper.addIfExist(TooltipHelper.Keys.PRIORITY, t, data);
                TooltipHelper.addIfExist(TooltipHelper.Keys.RANGE, TooltipHelper.Units.BLOCKS, t, data);
            });
        });
    }
}

package net.sinedkadis.terracompositio.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.ecf.burst.ECFBurstProjectileEntity;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class CultivationDesorberBlockEntity extends AbstractDesorberBlockEntity {

    private final ItemStackHandler renderStack = new ItemStackHandler();

    public CultivationDesorberBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.CULTIVATION_DESORBER_BE.get(), pos, state);
    }

    @Override
    protected int getMaxCFE() {
        return 32;
    }

    public void setRenderStack(ItemStack itemStack) {
        this.renderStack.setStackInSlot(0,itemStack);
    }

    public ItemStack getRenderStack() {
        return this.renderStack.getStackInSlot(0);
    }

    @SubscribeEvent
    public static void onCropGrowEvent(CropGrowEvent.Post event) {
        BlockPos pos = event.getPos();
        LevelAccessor level = event.getLevel();
        BlockState state = event.getState();
        ECFNetwork network = TerraCompositioAPI.instance().getECFNetworkInstance();
        Set<ECFNetworkMember> sources = network.getAllECFNetworkMembers((Level) level);
        List<CultivationDesorberBlockEntity> cultivators = sources.stream()
                .map(ECFNetworkMember::getEntityInstance)
                .map(IEntityInstance::tc$getBlockPos)
                .filter(ecfSourceBlockPose -> Math.sqrt(ecfSourceBlockPose.distSqr(pos)) < 7)
                .map(ecfSourceBlockPose -> {
                    if (level.getBlockEntity(ecfSourceBlockPose) instanceof CultivationDesorberBlockEntity blockEntity)
                        return blockEntity;
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        collectedList -> {
                            Collections.shuffle(collectedList);
                            return collectedList;
                        }
                ));
        int ECFToAdd = 1;
        for (CultivationDesorberBlockEntity blockEntity : cultivators){
            FluidTank fluidHandler1 = blockEntity.fluidHandler;
            if (!fluidHandler1.isEmpty() && fluidHandler1.getFluidAmount() >= ECFToAdd) {
                IECFHandler iecfHandler = blockEntity.ecfContainer();
                int added = iecfHandler.addECF(ECFToAdd, TransferAction.SIMULATE);
                ECFToAdd -= added;
                BlockPos blockEntityBlockPos = blockEntity.getBlockPos();
                ((Level) level).sendBlockUpdated(blockEntityBlockPos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                if (ECFToAdd == 0) {
                    blockEntity.setRenderStack(new ItemStack(
                            getDrops(state, new LootParams.Builder((ServerLevel) level)
                                    .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY))
                                    .getFirst().getItem().asItem())
                    );
                    fluidHandler1.drain(ECFToAdd, IFluidHandler.FluidAction.EXECUTE);
                    if (!level.isClientSide()) {
                        level.playSound(null, blockEntityBlockPos, SoundEvents.AZALEA_LEAVES_STEP, SoundSource.BLOCKS, 0.1f, 1f);
                        level.addFreshEntity(
                                Objects.requireNonNull(
                                        ECFBurstProjectileEntity.sendBurst(pos, iecfHandler, added, 5 / 20f)
                                )
                        );
                        ParticleHelperInternal.sendECFParticles((ServerLevel) level,
                                blockEntity.ecfContainer().getOffset().apply(blockEntityBlockPos.getCenter()),
                                pos.getCenter(),
                                added,
                                null,
                                5 / 20f);

                    }
                    break;
                }
            }
        }
    }

    protected static List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ResourceKey<LootTable> resourcekey = state.getBlock().getLootTable();
        if (resourcekey == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return loottable.getRandomItems(lootparams);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("render", renderStack.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        renderStack.deserializeNBT(registries, tag.getCompound("render"));
        super.loadAdditional(tag, registries);
    }
}

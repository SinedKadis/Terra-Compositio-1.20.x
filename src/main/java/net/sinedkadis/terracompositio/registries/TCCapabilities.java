package net.sinedkadis.terracompositio.registries;


import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.ecf.PlayerECFProvider;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderInstance;
import net.sinedkadis.terracompositio.util.ITCCapabilityProviderItem;
import org.jetbrains.annotations.Nullable;


/**
 * The Forge Caps, used in my mod.
 */
@EventBusSubscriber(modid = TerraCompositioAPI.MOD_ID)
public class TCCapabilities {
    public static final BlockCapability<IECFHandler, @Nullable Direction> ECF_HANDLER_BLOCK =
            BlockCapability.createSided(
                    TerraCompositio.modLoc("ecf_handler_block"),
                    IECFHandler.class
            );
    public static final EntityCapability<IECFHandler, @Nullable Void> ECF_HANDLER_ENTITY =
            EntityCapability.createVoid(
                    TerraCompositio.modLoc("ecf_handler_entity"),
                    IECFHandler.class
            );
    public static final ItemCapability<IECFHandler, @Nullable Void> ECF_HANDLER_ITEM =
            ItemCapability.createVoid(
                    TerraCompositio.modLoc("ecf_handler_item"),
                    IECFHandler.class
            );

    public static final BlockCapability<IItemHandler, @Nullable Direction> ITEM_STATE_HOLDER_BLOCK =
            BlockCapability.createSided(
                    TerraCompositio.modLoc("item_state_holder_block"),
                    IItemHandler.class
            );


    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> blockEntityTypeOpt : TCBlockEntities.BLOCK_ENTITIES.getEntries()) {
            BlockEntityType<?> blockEntityType = blockEntityTypeOpt.get();
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IItemHandler itemCapability = providerInstance.getItemCapability(cxt);
                            if (!itemCapability.equals(SentinelHelper.EMPTY_ITEM_HANDLER))
                                return itemCapability;
                        }
                        return null;
                    });
            event.registerBlockEntity(TCCapabilities.ECF_HANDLER_BLOCK, blockEntityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IECFHandler ecfCapability = providerInstance.getECFCapability(cxt);
                            if (!ecfCapability.equals(SentinelHelper.EMPTY_ECF_HANDLER))
                                return ecfCapability;
                        }
                        return null;
                    });
            event.registerBlockEntity(TCCapabilities.ITEM_STATE_HOLDER_BLOCK, blockEntityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IItemHandler stateHolderCapability = providerInstance.getStateHolderCapability(cxt);
                            if (!stateHolderCapability.equals(SentinelHelper.EMPTY_ITEM_HANDLER))
                                return stateHolderCapability;
                        }
                        return null;
                    });
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, blockEntityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IFluidHandler fluidCapability = providerInstance.getFluidCapability(cxt);
                            if (!fluidCapability.equals(SentinelHelper.EMPTY_ITEM_HANDLER))
                                return fluidCapability;
                        }
                        return null;
                    });
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, blockEntityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IEnergyStorage energyCapability = providerInstance.getEnergyCapability(cxt);
                            if (!energyCapability.equals(SentinelHelper.EMPTY_ENERGY_HANDLER)) return energyCapability;
                        }
                        return null;
                    });

        }
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder : TCEntities.ENTITY_TYPES.getEntries()) {
            EntityType<?> entityType = holder.get();
            event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, entityType,
                    (entity, cxt) -> {
                        if (entity instanceof ITCCapabilityProviderInstance providerInstance) {
                            IItemHandler itemCapability = providerInstance.getItemCapability(cxt);
                            if (!itemCapability.equals(SentinelHelper.EMPTY_ITEM_HANDLER))
                                return itemCapability;
                        }
                        return null;
                    });
            event.registerEntity(TCCapabilities.ECF_HANDLER_ENTITY, entityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IECFHandler ecfCapability = providerInstance.getECFCapability(null);
                            if (!ecfCapability.equals(SentinelHelper.EMPTY_ECF_HANDLER))
                                return ecfCapability;
                        }
                        return null;
                    });
            event.registerEntity(Capabilities.ItemHandler.ENTITY, entityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IItemHandler itemCapability = providerInstance.getItemCapability(null);
                            if (!itemCapability.equals(SentinelHelper.EMPTY_ITEM_HANDLER))
                                return itemCapability;
                        }
                        return null;
                    });
            event.registerEntity(Capabilities.EnergyStorage.ENTITY, entityType,
                    (be, cxt) -> {
                        if (be instanceof ITCCapabilityProviderInstance providerInstance) {
                            IEnergyStorage energyCapability = providerInstance.getEnergyCapability(cxt);
                            if (!energyCapability.equals(SentinelHelper.EMPTY_ENERGY_HANDLER))
                                return energyCapability;
                        }
                        return null;
                    });
        }
        for (DeferredHolder<Item, ? extends Item> holder : TCItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (item instanceof ITCCapabilityProviderItem providerInstance) {
                event.registerItem(TCCapabilities.ECF_HANDLER_ITEM,
                        (itemStack, cxt) -> providerInstance.addECFCapability(itemStack),item);
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (itemstack,cxt) -> providerInstance.addFluidCapability(itemstack),item);
            }
        }
        event.registerEntity(TCCapabilities.ECF_HANDLER_ENTITY, EntityType.PLAYER, new PlayerECFProvider());
    }
}

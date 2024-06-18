package net.sinedkadis.terracompositio.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.item.custom.FlowArmorItem;
import net.sinedkadis.terracompositio.item.custom.FlowBottleItem;
import net.sinedkadis.terracompositio.item.custom.ModArmorItem;
import net.sinedkadis.terracompositio.particle.ModParticles;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TerraCompositio.MOD_ID);

    public static final RegistryObject<Item> PEBBLE = ITEMS.register("pebble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_STAFF = ITEMS.register("stone_staff",
            () -> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public InteractionResult useOn(UseOnContext pContext) {
                    for (int i = 0; i<360;i++){
                        pContext.getLevel().addParticle(ModParticles.FLOW_PARTICLE.get(),
                                pContext.getClickedPos().getX() + 16D, pContext.getClickedPos().getY() + 18D, pContext.getClickedPos().getZ() + 18D, 0, -1, 0);

                    }
                    return InteractionResult.SUCCESS;
                }
            });
    public static final RegistryObject<Item> FLOW_BOTTLE = ITEMS.register("flow_bottle",
            () -> new FlowBottleItem(new Item.Properties().stacksTo(16).food(ModFoods.FLOW)));
    public static final RegistryObject<Item> OAK_STAFF = ITEMS.register("oak_staff",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NONFLOW_WOOD_HELMET = ITEMS.register("nonflow_wood_helmet",
            () -> new ModArmorItem(ModArmorMaterials.NONFLOW_WOOD, ArmorItem.Type.HELMET,new Item.Properties()));
    public static final RegistryObject<Item> NONFLOW_WOOD_CHESTPLATE = ITEMS.register("nonflow_wood_chestplate",
            () -> new ModArmorItem(ModArmorMaterials.NONFLOW_WOOD, ArmorItem.Type.CHESTPLATE,new Item.Properties()));
    public static final RegistryObject<Item> NONFLOW_WOOD_LEGGINGS = ITEMS.register("nonflow_wood_leggings",
            () -> new ModArmorItem(ModArmorMaterials.NONFLOW_WOOD, ArmorItem.Type.LEGGINGS,new Item.Properties()));
    public static final RegistryObject<Item> NONFLOW_WOOD_BOOTS = ITEMS.register("nonflow_wood_boots",
            () -> new ModArmorItem(ModArmorMaterials.NONFLOW_WOOD, ArmorItem.Type.BOOTS,new Item.Properties()));
    public static final RegistryObject<Item> FLOW_WOOD_HELMET = ITEMS.register("flow_wood_helmet",
            () -> new FlowArmorItem(ModArmorMaterials.FLOW_WOOD, ArmorItem.Type.HELMET,new Item.Properties()));
    public static final RegistryObject<Item> FLOW_WOOD_CHESTPLATE = ITEMS.register("flow_wood_chestplate",
            () -> new FlowArmorItem(ModArmorMaterials.FLOW_WOOD, ArmorItem.Type.CHESTPLATE,new Item.Properties()));
    public static final RegistryObject<Item> FLOW_WOOD_LEGGINGS = ITEMS.register("flow_wood_leggings",
            () -> new FlowArmorItem(ModArmorMaterials.FLOW_WOOD, ArmorItem.Type.LEGGINGS,new Item.Properties()));
    public static final RegistryObject<Item> FLOW_WOOD_BOOTS = ITEMS.register("flow_wood_boots",
            () -> new FlowArmorItem(ModArmorMaterials.FLOW_WOOD, ArmorItem.Type.BOOTS,new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}

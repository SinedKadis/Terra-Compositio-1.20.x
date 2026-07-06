package net.sinedkadis.terracompositio.registries;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.entity.custom.TCBoatEntity;
import net.sinedkadis.terracompositio.item.custom.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraCompositio.MOD_ID);

    //Cedar armor
    public static final DeferredItem<Item> FLOW_CEDAR_HELMET = ITEMS.register("flow_cedar_helmet",
            () -> new TCArmorItem(TCArmorMaterials.FLOW_CEDAR.value(), ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> FLOW_CEDAR_CHESTPLATE = ITEMS.register("flow_cedar_chestplate",
            () -> new TCArmorItem(TCArmorMaterials.FLOW_CEDAR.value(), ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<Item> FLOW_CEDAR_LEGGINGS = ITEMS.register("flow_cedar_leggings",
            () -> new TCArmorItem(TCArmorMaterials.FLOW_CEDAR.value(), ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<Item> FLOW_CEDAR_BOOTS = ITEMS.register("flow_cedar_boots",
            () -> new TCArmorItem(TCArmorMaterials.FLOW_CEDAR.value(), ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final DeferredItem<Item> FLOWING_FLOW_CEDAR_HELMET = ITEMS.register("flowing_flow_cedar_helmet",
            () -> new CedarArmorItem(TCArmorMaterials.FLOWING_FLOW_CEDAR.value(), ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> FLOWING_FLOW_CEDAR_CHESTPLATE = ITEMS.register("flowing_flow_cedar_chestplate",
            () -> new CedarArmorItem(TCArmorMaterials.FLOWING_FLOW_CEDAR.value(), ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<Item> FLOWING_FLOW_CEDAR_LEGGINGS = ITEMS.register("flowing_flow_cedar_leggings",
            () -> new CedarArmorItem(TCArmorMaterials.FLOWING_FLOW_CEDAR.value(), ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<Item> FLOWING_FLOW_CEDAR_BOOTS = ITEMS.register("flowing_flow_cedar_boots",
            () -> new CedarArmorItem(TCArmorMaterials.FLOWING_FLOW_CEDAR.value(), ArmorItem.Type.BOOTS, new Item.Properties()));


    //Flow infused iron materials
    public static final DeferredItem<Item> INFUSED_IRON_INGOT = ITEMS.register("infused_iron_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INFUSED_IRON_NUGGET = ITEMS.register("infused_iron_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INFUSED_IRON_ROD = ITEMS.register("infused_iron_rod",
            () -> new Item(new Item.Properties()));


    //Technetium
    public static final DeferredItem<Item> RAW_TECHNETIUM = ITEMS.register("technetium_raw_ore",
            () -> new UnstableTechnetiumItem(new Item.Properties(), 0, () -> 0));
    public static final DeferredItem<Item> LOW_ENRICHED_TECHNETIUM = ITEMS.register("low_enriched_technetium",
            () -> new UnstableTechnetiumItem(new Item.Properties(), 1, () -> 128));
    public static final DeferredItem<Item> MEDIUM_ENRICHED_TECHNETIUM = ITEMS.register("medium_enriched_technetium",
            () -> new UnstableTechnetiumItem(new Item.Properties(), 2, () -> 640));
    public static final DeferredItem<Item> HIGH_ENRICHED_TECHNETIUM = ITEMS.register("high_enriched_technetium",
            () -> new UnstableTechnetiumItem(new Item.Properties(), 3, () -> 3200));
    public static final DeferredItem<Item> TECHNETIUM_INGOT = ITEMS.register("technetium_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TECHNETIUM_NUGGET = ITEMS.register("technetium_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TECHNETIUM_ROD = ITEMS.register("technetium_rod",
            () -> new Item(new Item.Properties()));



    //Technetium armor
    public static final DeferredItem<Item> TECHNETIUM_CROWN = ITEMS.register("technetium_crown",
            () -> new TechnetiumArmorItem(ArmorItem.Type.HELMET,new Item.Properties()));
    public static final DeferredItem<Item> TECHNETIUM_CHESTPLATE = ITEMS.register("technetium_chestplate",
            () -> new TechnetiumArmorItem(ArmorItem.Type.CHESTPLATE,new Item.Properties()));
    public static final DeferredItem<Item> TECHNETIUM_LEGGINGS = ITEMS.register("technetium_leggings",
            () -> new TechnetiumArmorItem(ArmorItem.Type.LEGGINGS,new Item.Properties()));
    public static final DeferredItem<Item> TECHNETIUM_BOOTS = ITEMS.register("technetium_boots",
            () -> new TechnetiumArmorItem(ArmorItem.Type.BOOTS,new Item.Properties()));



    //Matter infuser
    public static final DeferredItem<Item> INPUT_BUS = ITEMS.register("lapis_input_bus",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> OUTPUT_BUS = ITEMS.register("copper_output_bus",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HALF_ROD = ITEMS.register("half_rod",
            () -> new Item(new Item.Properties()));



    //Wrench Axe
    public static final DeferredItem<Item> WRENCH_AXE = ITEMS.register("flow_rotating_axe",
            () -> new WrenchAxeItem(Tiers.IRON, new Item.Properties().durability(330)));
    public static final DeferredItem<Item> WRENCH_TAG_HOLDER = ITEMS.register("wrench_tag_holder",
            () -> new Item(new Item.Properties()));





    //Copper materials
    public static final DeferredItem<Item> COPPER_NUGGET = ITEMS.register("copper_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_ROD = ITEMS.register("copper_rod",
            () -> new Item(new Item.Properties()));


    //Signs
    public static final DeferredItem<Item> FLOW_CEDAR_SIGN = ITEMS.register("flow_cedar_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), TCBlocks.FLOW_CEDAR_SIGN.get(), TCBlocks.FLOW_CEDAR_WALL_SIGN.get()));
    public static final DeferredItem<Item> FLOW_CEDAR_HANGING_SIGN = ITEMS.register("flow_cedar_hanging_sign",
            () -> new HangingSignItem(TCBlocks.FLOW_CEDAR_HANGING_SIGN.get(), TCBlocks.FLOW_CEDAR_WALL_HANGING_SIGN.get(),new Item.Properties().stacksTo(16)));


    //Boats
    public static final DeferredItem<Item> FLOW_CEDAR_BOAT = ITEMS.register("flow_cedar_boat",
            () -> new TCBoatItem(false, TCBoatEntity.Type.FLOW_CEDAR, new Item.Properties()));
    public static final DeferredItem<Item> FLOW_CEDAR_CHEST_BOAT = ITEMS.register("flow_cedar_chest_boat",
            () -> new TCBoatItem(true, TCBoatEntity.Type.FLOW_CEDAR, new Item.Properties()));


    //Misc items
    public static final DeferredItem<Item> GOLD_ROD = ITEMS.register("gold_rod",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FLOW_CEDAR_ENT_SPAWN_EGG = ITEMS.register("flow_cedar_ent_spawn_egg",
            () -> new DeferredSpawnEggItem(TCEntities.FLOW_CEDAR_ENT, 0x352001, 0x015161, new Item.Properties()));

    //Special
    public static final DeferredItem<Item> FLOW_BOTTLE = ITEMS.register("flow_bottle",
            () -> new FlowBottleItem(new Item.Properties().stacksTo(16).food(TCFoods.FLOW)));
    public static final DeferredItem<Item> FLOW_INFUSER_KIT = ITEMS.register("flow_infuser_kit",
            () -> new Item(new Item.Properties()){
                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                    tooltipComponents.add(Component.translatable("item.terracompositio.flow_infuser_kit.tooltip").withStyle(ChatFormatting.GRAY));
                }
            });
    public static final DeferredItem<Item> SHIELDED_BUNDLE = ITEMS.register("shielded_bundle",
            () -> new ShieldedBundleItem(new Item.Properties()));
    public static final DeferredItem<Item> CREATION_FLOW_JOURNAL = ITEMS.register("creation_flow_journal",
            () -> new CreationFlowJournalItem(new Item.Properties()));
    public static final DeferredItem<Item> FLUID_APPLIER = ITEMS.register("fluid_applier",
            () -> new FluidApplierItem(new Item.Properties()));
    public static final DeferredItem<Item> ECF_CHARGE = ITEMS.register("ecf_charge",
            () -> new ECFBallItem(new Item.Properties()));
    public static final DeferredItem<Item> INFUSED_FERTILIZER = ITEMS.register("infused_fertilizer",
            () -> new InfusedFertilizerItem(new Item.Properties()));

    //Apples
    public static final DeferredItem<Item> APPLE_OF_KNOWLEDGE = ITEMS.register("apple_of_knowledge",
            () -> new KnowledgeAppleItem(new Item.Properties()
                    .food((TCFoods.CREATION_KNOWLEDGE))));
    public static final DeferredItem<Item> APPLE_OF_IGNORANCE = ITEMS.register("apple_of_ignorance",
            () -> new KnowledgeAppleItem(new Item.Properties()
                    .food((TCFoods.IGNORANCE))));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}

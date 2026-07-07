package net.sinedkadis.terracompositio.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCFluids;
import net.sinedkadis.terracompositio.registries.TCItems;

import java.util.LinkedHashMap;
import java.util.Objects;

public class TCItemModelProvider extends ItemModelProvider {
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }
    public TCItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        trapdoorItem(TCBlocks.FLOW_CEDAR_TRAPDOOR);
        fenceItem(TCBlocks.FLOW_CEDAR_FENCE, TCBlocks.FLOW_CEDAR_PLANKS);
        simpleBlockItem(TCBlocks.FLOW_CEDAR_DOOR);
        buttonItem(TCBlocks.FLOW_CEDAR_BUTTON, TCBlocks.FLOW_CEDAR_PLANKS);
        evenSimplerBlockItem(TCBlocks.FLOW_CEDAR_STAIRS);
        evenSimplerBlockItem(TCBlocks.FLOW_CEDAR_SLAB);
        evenSimplerBlockItem(TCBlocks.FLOW_CEDAR_PRESSURE_PLATE);
        evenSimplerBlockItem(TCBlocks.FLOW_CEDAR_FENCE_GATE);
        simpleItem(TCFluids.FLOW_FLUID.bucket);
        simpleItem(TCItems.FLOW_BOTTLE);
        simpleItem(TCFluids.BIRCH_JUICE_FLUID.bucket);

        trimmedArmorItem(TCItems.FLOW_CEDAR_HELMET);
        trimmedArmorItem(TCItems.FLOW_CEDAR_CHESTPLATE);
        trimmedArmorItem(TCItems.FLOW_CEDAR_LEGGINGS);
        trimmedArmorItem(TCItems.FLOW_CEDAR_BOOTS);

        trimmedArmorItem(TCItems.FLOWING_FLOW_CEDAR_HELMET);
        trimmedArmorItem(TCItems.FLOWING_FLOW_CEDAR_CHESTPLATE);
        trimmedArmorItem(TCItems.FLOWING_FLOW_CEDAR_LEGGINGS);
        trimmedArmorItem(TCItems.FLOWING_FLOW_CEDAR_BOOTS);

        simpleItem(TCItems.FLOW_CEDAR_SIGN);
        simpleItem(TCItems.FLOW_CEDAR_HANGING_SIGN);

        simpleItem(TCItems.FLOW_CEDAR_BOAT);
        simpleItem(TCItems.FLOW_CEDAR_CHEST_BOAT);

        simpleItem(TCItems.INFUSED_IRON_INGOT);
        simpleItem(TCItems.INFUSED_IRON_NUGGET);
        simpleItem(TCItems.COPPER_NUGGET);
        simpleItem(TCItems.FLOW_INFUSER_KIT);
        simpleItem(TCItems.RAW_TECHNETIUM);
        simpleItem(TCItems.LOW_ENRICHED_TECHNETIUM);
        simpleItem(TCItems.MEDIUM_ENRICHED_TECHNETIUM);
        simpleItem(TCItems.HIGH_ENRICHED_TECHNETIUM);
        simpleItem(TCItems.INFUSED_IRON_ROD);
        simpleItem(TCItems.GOLD_ROD);
        simpleItem(TCItems.INPUT_BUS);
        simpleItem(TCItems.OUTPUT_BUS);
        simpleItem(TCItems.COPPER_ROD);
        simpleItem(TCItems.HALF_ROD);

        simpleItem(TCItems.TECHNETIUM_INGOT);
        simpleItem(TCItems.TECHNETIUM_ROD);
        simpleItem(TCItems.TECHNETIUM_NUGGET);
        simpleItem(TCItems.WRENCH_TAG_HOLDER);

        simpleItem(TCItems.ECF_CHARGE);
        simpleItem(TCItems.INFUSED_FERTILIZER);

        trimmedArmorItem(TCItems.TECHNETIUM_CHESTPLATE);
        trimmedArmorItem(TCItems.TECHNETIUM_LEGGINGS);
        trimmedArmorItem(TCItems.TECHNETIUM_BOOTS);

        simpleItem(TCItems.APPLE_OF_KNOWLEDGE);
        simpleItem(TCItems.APPLE_OF_IGNORANCE);


        saplingItem(TCBlocks.FLOW_CEDAR_SAPLING);

        withExistingParent(Objects.requireNonNull(TCItems.FLOW_CEDAR_ENT_SPAWN_EGG.getId()).getPath(), mcLoc("item/template_spawn_egg"));

        if (ModList.get().isLoaded("create")) {
            TerraCompositio.createCompat.getDataGen().registerItemModels();
        }
    }

    // Shoutout to El_Redstoniano for making this
    private void trimmedArmorItem(DeferredItem<Item> itemRegistryObject) {

        if(itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {

                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = "item/" + armorItem;
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = TerraCompositio.modLoc(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.tryParse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = TerraCompositio.modLoc(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                assert trimResLoc != null;
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc)
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(Objects.requireNonNull(itemRegistryObject.getId()).getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                TerraCompositio.modLoc(
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
    }


    private void simpleItem(DeferredItem<Item> item) {
        withExistingParent(Objects.requireNonNull(item.getId()).getPath(),
                Objects.requireNonNull(ResourceLocation.tryParse("item/generated"))).texture("layer0",
                TerraCompositio.modLoc( "item/" + item.getId().getPath()));
    }

    private void saplingItem(DeferredBlock<Block> item) {
        withExistingParent(Objects.requireNonNull(item.getId()).getPath(),
                Objects.requireNonNull(ResourceLocation.tryParse("item/generated"))).texture("layer0",
                TerraCompositio.modLoc("block/" + item.getId().getPath()));
    }

    public void evenSimplerBlockItem(DeferredBlock<Block> block) {
        ResourceLocation key = block.getId();
        this.withExistingParent(TerraCompositio.MOD_ID + ":" + key.getPath(),
                modLoc("block/" + key.getPath()));
    }

    public void trapdoorItem(DeferredBlock<Block> block) {
        ResourceLocation key = block.getId();
        this.withExistingParent(key.getPath(),
                modLoc("block/" + key.getPath() + "_bottom"));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        ResourceLocation key = block.getId();
        this.withExistingParent(key.getPath(), mcLoc("block/fence_inventory"))
                .texture("texture", TerraCompositio.modLoc( "block/" + baseBlock.getId().getPath()));
    }

    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        ResourceLocation key = block.getId();
        ResourceLocation key1 = baseBlock.getId();
        this.withExistingParent(key.getPath(), mcLoc("block/button_inventory"))
                .texture("texture", TerraCompositio.modLoc("block/" + key1.getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        ResourceLocation key = block.getId();
        ResourceLocation key1 = baseBlock.getId();
        this.withExistingParent(key.getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", TerraCompositio.modLoc("block/" + key1.getPath()));
    }

    private void simpleBlockItem(DeferredBlock<Block> item) {
        withExistingParent(Objects.requireNonNull(item.getId()).getPath(),
                Objects.requireNonNull(ResourceLocation.tryParse("item/generated"))).texture("layer0",
                TerraCompositio.modLoc("item/" + item.getId().getPath()));
    }

}

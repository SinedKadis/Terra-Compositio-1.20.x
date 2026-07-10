package net.sinedkadis.terracompositio.datagen;


import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.datagen.builders.AltarTransformationRecipeBuilder;
import net.sinedkadis.terracompositio.datagen.builders.FlowInfusionRecipeBuilder;
import net.sinedkadis.terracompositio.datagen.builders.MatterInfusionRecipeBuilder;
import net.sinedkadis.terracompositio.datagen.builders.TechnetiumFiringRecipeBuilder;
import net.sinedkadis.terracompositio.recipe.ECFStorageUpgradeRecipe;
import net.sinedkadis.terracompositio.recipe.TagTransferShapedRecipe;
import net.sinedkadis.terracompositio.recipe.WrapperResult;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCDataComponents;
import net.sinedkadis.terracompositio.registries.TCItems;
import net.sinedkadis.terracompositio.registries.TCTags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static net.minecraft.data.recipes.RecipeBuilder.getDefaultRecipeId;

@ParametersAreNonnullByDefault
public class TCRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public TCRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(pOutput,provider);
    }

    @Override
    protected void buildRecipes(RecipeOutput pWriter) {

        specialCraftingRecipe(pWriter, ECFStorageUpgradeRecipe::new,"storage_upgrade");

        buildCedarBlocks(pWriter);
        buildMatterInfuserBlocks(pWriter);
        buildCopperMaterials(pWriter);
        buildTechnetiumMaterials(pWriter);
        buildTechnetiumArmor(pWriter);
        buildCedarArmor(pWriter);
        buildInfusedIronMaterials(pWriter);
        buildGoldMaterials(pWriter);
        buildDesorbers(pWriter);
        buildPathPointers(pWriter);
        buildFloatingRedstone(pWriter);
        buildCFJ(pWriter);


        buildTechnetiumOreProcessing(pWriter);
        buildMisc(pWriter);
        buildSpecial(pWriter);
        buildApples(pWriter);

        buildCompat(pWriter);


    }

    private static void buildCFJ(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_ALTAR.get())
                .pattern("LLL")
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);


        ItemStack bookLevel1 = createCFJBook(1);
        ItemStack bookLevel2 = createCFJBook(2);
        ItemStack bookLevel3 = createCFJBook(3);
        ItemStack bookLevel4 = createCFJBook(4);
        ItemStack bookLevel5 = createCFJBook(5);


        AltarTransformationRecipeBuilder.create(
                        bookLevel1,
                        Ingredient.of(Items.BOOK)
                )
                .save(pWriter, "upgrade_book_to_day_1");
        AltarTransformationRecipeBuilder.create(
                        bookLevel2,
                        DataComponentIngredient.of(true,
                                bookLevel1
                        ),
                        Ingredient.of(TCBlocks.FLOW_CEDAR_SAPLING.get().asItem().getDefaultInstance())
                )
                .save(pWriter, "upgrade_book_to_day_2");
        AltarTransformationRecipeBuilder.create(
                        bookLevel3,
                        DataComponentIngredient.of(true,
                                bookLevel2
                        ),
                        Ingredient.of(TCItems.FLOW_BOTTLE.get().asItem().getDefaultInstance())
                )
                .save(pWriter, "upgrade_book_to_day_3");
        AltarTransformationRecipeBuilder.create(
                        bookLevel4,
                        DataComponentIngredient.of(true,
                                bookLevel3
                        ),
                        Ingredient.of(TCItems.ECF_CHARGE.get().asItem().getDefaultInstance())
                )
                .save(pWriter, "upgrade_book_to_day_4");
        AltarTransformationRecipeBuilder.create(
                        bookLevel5,
                        DataComponentIngredient.of(true,
                                bookLevel4
                        ),
                        Ingredient.of(TCItems.TECHNETIUM_INGOT.get().asItem().getDefaultInstance())
                )
                .save(pWriter, "upgrade_book_to_day_5");
    }

    private void buildApples(RecipeOutput pWriter) {
        FlowInfusionRecipeBuilder.create(
                TCItems.APPLE_OF_KNOWLEDGE.get().getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY,Ingredient.of(Items.APPLE)),
                100,
                200
        ).save(pWriter, TerraCompositio.modLoc("flow_infusion/apple_of_knowledge"));
        FlowInfusionRecipeBuilder.create(
                TCItems.APPLE_OF_IGNORANCE.get().getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY,Ingredient.of(Items.GOLDEN_APPLE)),
                100,
                200
        ).save(pWriter, TerraCompositio.modLoc("flow_infusion/apple_of_ignorance"));
    }

    private void buildCompat(RecipeOutput pWriter) {
        if (ModList.get().isLoaded("create")) {
            TerraCompositio.createCompat.getDataGen().buildRecipes(this, pWriter);
        }
    }

    private static void buildCedarBlocks(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_STAIRS.get())
                .pattern("S  ")
                .pattern("SS ")
                .pattern("SSS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_SLAB.get(),2)
                .pattern("SSS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_PRESSURE_PLATE.get())
                .pattern("SS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_FENCE.get())
                .pattern("SFS")
                .pattern("SFS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .define('F', Items.STICK)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_FENCE_GATE.get())
                .pattern("FSF")
                .pattern("FSF")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .define('F', Items.STICK)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_DOOR.get(),3)
                .pattern("SS")
                .pattern("SS")
                .pattern("SS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_TRAPDOOR.get(),2)
                .pattern("SSS")
                .pattern("SSS")
                .define('S', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_PLANKS.get(), 4)
                .requires(TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_BUTTON.get(), 1)
                .requires(TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_WOOD.get(), 3)
                .pattern("SS")
                .pattern("SS")
                .define('S', TCBlocks.FLOW_CEDAR_LOG.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_SIGN, 3)
                .pattern("PPP")
                .pattern("PPP")
                .pattern(" S ")
                .define('P', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOW_CEDAR_HANGING_SIGN, 6)
                .pattern("C C")
                .pattern("LLL")
                .pattern("LLL")
                .define('L', TCBlocks.STRIPPED_FLOW_CEDAR_LOG.get())
                .define('C', Items.CHAIN)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, TCItems.FLOW_CEDAR_BOAT.get())
                .pattern("L L")
                .pattern("LLL")
                .define('L', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, TCItems.FLOW_CEDAR_CHEST_BOAT, 1)
                .requires(TCItems.FLOW_CEDAR_BOAT.get())
                .requires(Items.CHEST)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_PLANKS.get()), has(TCBlocks.FLOW_CEDAR_PLANKS.get()))
                .save(pWriter);
    }

    private static void buildTechnetiumOreProcessing(RecipeOutput pWriter) {
        int base1 = 128;
        int consumeMultiplier = 5;
        int generationMultiplier = 4;

        int base2 = base1 * consumeMultiplier;
        int base3 = base2 * consumeMultiplier;

        MatterInfusionRecipeBuilder.create(
                TCItems.RAW_TECHNETIUM.get(),4,
                        TCItems.LOW_ENRICHED_TECHNETIUM.get(),1,
                        Items.COAL, base1, 200, 30)
                .save(pWriter);

        MatterInfusionRecipeBuilder.create(
                        TCItems.LOW_ENRICHED_TECHNETIUM.get(),4,
                        TCItems.MEDIUM_ENRICHED_TECHNETIUM.get(),1,
                        Items.REDSTONE, base2, 1000, 30)
                .save(pWriter);

        MatterInfusionRecipeBuilder.create(
                        TCItems.MEDIUM_ENRICHED_TECHNETIUM.get(),4,
                        TCItems.HIGH_ENRICHED_TECHNETIUM.get(),1,
                        Items.DIAMOND, base3, 2000, 30)
                .save(pWriter);

        TechnetiumFiringRecipeBuilder.create(
                        TCItems.LOW_ENRICHED_TECHNETIUM.get(),
                        base1 * generationMultiplier
                )
                .save(pWriter, TCItems.LOW_ENRICHED_TECHNETIUM.getId().withPrefix("firing/"));
        TechnetiumFiringRecipeBuilder.create(
                        TCItems.MEDIUM_ENRICHED_TECHNETIUM.get(),
                        base2 * generationMultiplier)
                .save(pWriter, TCItems.MEDIUM_ENRICHED_TECHNETIUM.getId().withPrefix("firing/"));
        TechnetiumFiringRecipeBuilder.create(
                        TCItems.HIGH_ENRICHED_TECHNETIUM.get(),
                        base3 * generationMultiplier)
                .save(pWriter, TCItems.HIGH_ENRICHED_TECHNETIUM.getId().withPrefix("firing/"));


        oreSmelting(pWriter,
                List.of(TCItems.RAW_TECHNETIUM.get(),
                        TCBlocks.TECHNETIUM_ORE.get(),
                        TCBlocks.TECHNETIUM_DEEPSLATE_ORE.get()),
                RecipeCategory.MISC,
                TCItems.TECHNETIUM_INGOT.get(),
                0.25f,
                200,
                "technetium");
        oreSmelting(pWriter,
                List.of(TCItems.LOW_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.RAW_TECHNETIUM.get(),
                0.25f,
                200,
                "technetium");
        oreSmelting(pWriter,
                List.of(TCItems.MEDIUM_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.LOW_ENRICHED_TECHNETIUM.get(),
                0.25f,
                200,
                "technetium");
        oreSmelting(pWriter,
                List.of(TCItems.HIGH_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.MEDIUM_ENRICHED_TECHNETIUM.get(),
                0.25f,
                200,
                "technetium");

        oreBlasting(pWriter,
                List.of(TCItems.RAW_TECHNETIUM.get(),
                        TCBlocks.TECHNETIUM_ORE.get(),
                        TCBlocks.TECHNETIUM_DEEPSLATE_ORE.get()),
                RecipeCategory.MISC,
                TCItems.TECHNETIUM_INGOT.get(),
                0.25f,
                100,
                "technetium");
        oreBlasting(pWriter,
                List.of(TCItems.LOW_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.RAW_TECHNETIUM.get(),
                0.25f,
                100,
                "technetium");
        oreBlasting(pWriter,
                List.of(TCItems.MEDIUM_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.LOW_ENRICHED_TECHNETIUM.get(),
                0.25f,
                100,
                "technetium");
        oreBlasting(pWriter,
                List.of(TCItems.HIGH_ENRICHED_TECHNETIUM.get()),
                RecipeCategory.MISC,
                TCItems.MEDIUM_ENRICHED_TECHNETIUM.get(),
                0.25f,
                100,
                "technetium");
    }

    private void buildTechnetiumArmor(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_CROWN.get())
                .pattern("T T")
                .pattern("TTT")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_CROWN.get())
                .pattern("T T")
                .pattern("TTT")
                .pattern(" C ")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('C', ItemTags.HEAD_ARMOR)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(WrapperResult.of(pWriter,TagTransferShapedRecipe::new),
                        TerraCompositio.modLoc("with_tag/" + getDefaultRecipeId(TCItems.TECHNETIUM_CROWN.get()).getPath()));


        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_CHESTPLATE.get())
                .pattern("T T")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_CHESTPLATE.get())
                .pattern("TCT")
                .pattern("TTT")
                .pattern("TTT")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('C', ItemTags.CHEST_ARMOR)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(WrapperResult.of(pWriter,TagTransferShapedRecipe::new),
                        TerraCompositio.modLoc("with_tag/" + getDefaultRecipeId(TCItems.TECHNETIUM_CHESTPLATE.get()).getPath()));


        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_LEGGINGS.get())
                .pattern("TTT")
                .pattern("T T")
                .pattern("T T")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_LEGGINGS.get())
                .pattern("TTT")
                .pattern("TCT")
                .pattern("T T")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('C', ItemTags.LEG_ARMOR)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(WrapperResult.of(pWriter,TagTransferShapedRecipe::new),
                        TerraCompositio.modLoc("with_tag/" + getDefaultRecipeId(TCItems.TECHNETIUM_LEGGINGS.get()).getPath()));


        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_BOOTS.get())
                .pattern("T T")
                .pattern("T T")
                .pattern("F F")
                .define('F', Items.FEATHER)
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.TECHNETIUM_BOOTS.get())
                .pattern("T T")
                .pattern("TCT")
                .pattern("F F")
                .define('F', Items.FEATHER)
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('C', ItemTags.FOOT_ARMOR)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(WrapperResult.of(pWriter,TagTransferShapedRecipe::new),
                        TerraCompositio.modLoc("with_tag/" + getDefaultRecipeId(TCItems.TECHNETIUM_BOOTS.get()).getPath()));
    }

    private static void buildPathPointers(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_COLLECTOR.get())
                .pattern(" T ")
                .pattern("LLT")
                .pattern(" T ")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_EMITTER.get())
                .pattern(" TR")
                .pattern("TLI")
                .pattern(" TR")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('I', TCItems.INFUSED_IRON_INGOT.get())
                .define('R', TCItems.INFUSED_IRON_ROD.get())
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_SENDER.get())
                .pattern(" TC")
                .pattern("TC ")
                .pattern(" TC")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_RECEIVER.get())
                .pattern("LT ")
                .pattern(" LT")
                .pattern("LT ")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('L', Items.LAPIS_LAZULI)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_EXTRACTOR.get())
                .pattern("LT ")
                .pattern("NLT")
                .pattern("LT ")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('N', TCItems.INFUSED_IRON_NUGGET.get())
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.PP_INFUSER.get())
                .pattern(" TR")
                .pattern("TLR")
                .pattern(" TR")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('R', TCItems.INFUSED_IRON_ROD.get())
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
    }

    private static void buildWrenchAxe(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.WRENCH_AXE.get())
                .pattern("II")
                .pattern("IS")
                .pattern(" S")
                .define('I', TCItems.INFUSED_IRON_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.WRENCH_AXE.get())
                .pattern("II")
                .pattern("SI")
                .pattern("S ")
                .define('I', TCItems.INFUSED_IRON_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter, Objects.requireNonNull(ResourceLocation.tryBuild(Objects.requireNonNull(TCItems.WRENCH_AXE.getId()).getNamespace(),
                        Objects.requireNonNull(TCItems.WRENCH_AXE.getId()).getPath() + "_mirrored")));
    }

    private static void buildMisc(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BUNDLE)
                .pattern("S")
                .pattern("L")
                .define('S', Items.STRING)
                .define('L',Items.LEATHER)
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                .save(pWriter);
        FlowInfusionRecipeBuilder.create(
                TCItems.ECF_CHARGE.get().getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY,Ingredient.of(Items.SNOWBALL)),
                10,
                20
        ).save(pWriter, TerraCompositio.modLoc("flow_infusion/ecf_charge"));
        TechnetiumFiringRecipeBuilder.create(
                        TCItems.ECF_CHARGE.get(),
                        8)
                .save(pWriter, TerraCompositio.modLoc("firing/ecf_charge"));
        oreSmelting(pWriter,
                List.of(TCItems.ECF_CHARGE.get()),
                RecipeCategory.MISC,
                Items.SNOWBALL,
                0.0f,
                40,
                "technetium");
        cookSmoking(pWriter,
                List.of(TCItems.ECF_CHARGE.get()),
                RecipeCategory.MISC,
                Items.SNOWBALL,
                0.0f,
                20,
                "technetium");
        FlowInfusionRecipeBuilder.create(
                TCItems.INFUSED_FERTILIZER.get().getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY,Ingredient.of(ItemTags.VILLAGER_PLANTABLE_SEEDS)),
                200,
                200
        ).save(pWriter, TerraCompositio.modLoc("flow_infusion/infused_fertilizer"));
    }

    private static void buildDesorbers(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.CONSTRUCTION_DESORBER.get())
                .pattern("ILI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.CULTIVATION_DESORBER.get())
                .pattern("ILI")
                .pattern("III")
                .define('I', Items.COPPER_INGOT)
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlocks.TIME_PASSAGE_DESORBER.get())
                .pattern(" R ")
                .pattern("ILI")
                .pattern("III")
                .define('I', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .define('L', TCTags.Items.FLOW_CEDAR_LOGS)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
    }

    private static void buildGoldMaterials(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.GOLD_ROD.get(), 2)
                .pattern("R")
                .pattern("R")
                .define('R', Items.GOLD_INGOT)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);
    }

    private static void buildInfusedIronMaterials(RecipeOutput pWriter) {
        ResourceLocation key = TCItems.INFUSED_IRON_INGOT.getId();

        FlowInfusionRecipeBuilder.create(
                TCItems.INFUSED_IRON_INGOT.get().getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY,Ingredient.of(Items.IRON_INGOT)),
                50,
                100
        ).save(pWriter,TerraCompositio.modLoc("flow_infusion/infused_iron"));


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.INFUSED_IRON_INGOT.get(), 1)
                .requires(TCItems.INFUSED_IRON_NUGGET.get(),9)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter,Objects.requireNonNull(ResourceLocation.tryBuild(Objects.requireNonNull(key).getNamespace(),
                        Objects.requireNonNull(key).getPath() + "_from_nugget")));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCBlocks.INFUSED_IRON_BLOCK.get(), 1)
                .requires(TCItems.INFUSED_IRON_INGOT.get(),9)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.INFUSED_IRON_INGOT.get(), 9)
                .requires(TCBlocks.INFUSED_IRON_BLOCK.get(),1)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter,Objects.requireNonNull(ResourceLocation.tryBuild(Objects.requireNonNull(key).getNamespace(),
                        Objects.requireNonNull(key).getPath() + "_from_block")));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.INFUSED_IRON_ROD.get(), 2)
                .pattern("R")
                .pattern("R")
                .define('R', TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.INFUSED_IRON_NUGGET.get(), 9)
                .requires(TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
    }

    private static void buildCedarArmor(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.FLOW_CEDAR_BOOTS.get())
                .pattern("WLW")
                .pattern("W W")
                .define('W', TCTags.Items.FLOW_CEDAR_LOGS)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.FLOW_CEDAR_LEGGINGS.get())
                .pattern("WWW")
                .pattern("WLW")
                .pattern("W W")
                .define('W', TCTags.Items.FLOW_CEDAR_LOGS)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.FLOW_CEDAR_CHESTPLATE.get())
                .pattern("WLW")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', TCTags.Items.FLOW_CEDAR_LOGS)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItems.FLOW_CEDAR_HELMET.get())
                .pattern("WWW")
                .pattern("WLW")
                .define('W', TCTags.Items.FLOW_CEDAR_LOGS)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(TCBlocks.FLOW_CEDAR_LOG.get()), has(TCBlocks.FLOW_CEDAR_LOG.get()))
                .save(pWriter);
    }

    private static void buildTechnetiumMaterials(RecipeOutput pWriter) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.RAW_TECHNETIUM.get(), 9)
                .requires(TCBlocks.TECHNETIUM_RAW_ORE_BLOCK.get())
                .unlockedBy(getHasName(TCBlocks.TECHNETIUM_RAW_ORE_BLOCK.get()), has(TCBlocks.TECHNETIUM_RAW_ORE_BLOCK.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCBlocks.TECHNETIUM_RAW_ORE_BLOCK.get(), 1)
                .requires(TCItems.RAW_TECHNETIUM.get(),9)
                .unlockedBy(getHasName(TCItems.RAW_TECHNETIUM.get()), has(TCItems.RAW_TECHNETIUM.get()))
                .save(pWriter);
        ResourceLocation technetium = TCItems.TECHNETIUM_INGOT.getId();
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.TECHNETIUM_INGOT.get(), 1)
                .requires(TCItems.TECHNETIUM_NUGGET.get(),9)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter,Objects.requireNonNull(ResourceLocation.tryBuild(Objects.requireNonNull(technetium).getNamespace(),
                        Objects.requireNonNull(technetium).getPath() + "_from_nugget")));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCBlocks.TECHNETIUM_BLOCK.get(), 1)
                .requires(TCItems.TECHNETIUM_INGOT.get(),9)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.TECHNETIUM_INGOT.get(), 9)
                .requires(TCBlocks.TECHNETIUM_BLOCK.get(),1)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter,Objects.requireNonNull(ResourceLocation.tryBuild(Objects.requireNonNull(technetium).getNamespace(),
                        Objects.requireNonNull(technetium).getPath() + "_from_block")));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.TECHNETIUM_ROD.get(), 2)
                .pattern("R")
                .pattern("R")
                .define('R', TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.TECHNETIUM_NUGGET.get(), 9)
                .requires(TCItems.TECHNETIUM_INGOT.get())
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
    }

    private static void buildCopperMaterials(RecipeOutput pWriter) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCItems.COPPER_NUGGET.get(), 9)
                .requires(Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.COPPER_ROD.get(), 2)
                .pattern("R")
                .pattern("R")
                .define('R', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.COPPER_INGOT, 2)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', TCItems.COPPER_NUGGET.get())
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);
    }

    private static void buildSpecial(RecipeOutput pWriter) {
        buildWrenchAxe(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.WEDGE.get())
                .pattern("S S")
                .pattern("SSS")
                .pattern(" S ")
                .define('S', Items.IRON_NUGGET)
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.FLOW_INFUSER_KIT.get())
                .pattern(" N ")
                .pattern("SNS")
                .pattern(" N ")
                .define('S', Items.STICK)
                .define('N', TCTags.Items.COPPER_NUGGETS)
                .unlockedBy(getHasName(Items.STICK), has(TCItems.COPPER_NUGGET.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, TCItems.SHIELDED_BUNDLE.get(), 1)
                .requires(Items.BUNDLE)
                .requires(TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(Items.BUNDLE))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.FLUID_APPLIER.get())
                .pattern("  N")
                .pattern(" T ")
                .pattern("S  ")
                .define('T', TCItems.TECHNETIUM_INGOT.get())
                .define('N', TCItems.INFUSED_IRON_NUGGET.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(TCItems.TECHNETIUM_INGOT.get()), has(TCItems.TECHNETIUM_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TCBlocks.AIR_SATURATOR.get())
                .pattern(" I ")
                .pattern("RCR")
                .pattern(" L ")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COBWEB)
                .define('R', TCItems.INFUSED_IRON_ROD.get())
                .define('L', TCBlocks.FLOW_CEDAR_LOG.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
    }

    private static void buildMatterInfuserBlocks(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.MATTER_INFUSER_PORT.get())
                .pattern(" R ")
                .pattern("RFR")
                .pattern(" R ")
                .define('R', TCItems.COPPER_ROD.get())
                .define('F',Items.ITEM_FRAME)
                .unlockedBy(getHasName(Items.ITEM_FRAME), has(Items.ITEM_FRAME))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.MATTER_INFUSER_UNIT.get())
                .pattern("RRR")
                .pattern(" I ")
                .pattern("RRR")
                .define('R', TCItems.COPPER_ROD.get())
                .define('I',TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(Items.ITEM_FRAME), has(Items.ITEM_FRAME))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.OUTPUT_BUS.get())
                .pattern(" R ")
                .pattern("RHR")
                .pattern(" R ")
                .define('R', TCItems.COPPER_ROD.get())
                .define('H', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCItems.INPUT_BUS.get())
                .pattern(" R ")
                .pattern("RHR")
                .pattern(" R ")
                .define('R', Items.LAPIS_LAZULI)
                .define('H', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);
    }

    private void buildFloatingRedstone(RecipeOutput pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.FLOATING_REDSTONE.get())
                .pattern("R")
                .pattern("N")
                .define('R', Items.REDSTONE)
                .define('N', TCItems.INFUSED_IRON_NUGGET.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.FLOATING_TORCH_HOLDER.get())
                .pattern("P")
                .pattern("I")
                .define('P', Items.FLOWER_POT)
                .define('I', TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.FLOATING_REPEATER.get())
                .pattern("TRT")
                .pattern("SIS")
                .define('T', Items.REDSTONE_TORCH)
                .define('R', Items.REDSTONE)
                .define('S', Items.STONE)
                .define('I',TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.FLOATING_COMPARATOR.get())
                .pattern(" T ")
                .pattern("TQT")
                .pattern("SIS")
                .define('T', Items.REDSTONE_TORCH)
                .define('Q', Items.QUARTZ)
                .define('S', Items.STONE)
                .define('I',TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.INFUSED_IRON_PRESSURE_PLATE.get())
                .pattern("II")
                .define('I',TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.INFUSED_IRON_DOOR.get())
                .pattern("II")
                .pattern("II")
                .pattern("II")
                .define('I',TCItems.INFUSED_IRON_INGOT.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TCBlocks.FLOATING_BUTTON.get(), 1)
                .requires(TCItems.INFUSED_IRON_INGOT.get())
                .requires(ItemTags.STONE_BUTTONS)
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TCBlocks.FLOATING_LEVER.get())
                .pattern("n")
                .pattern("p")
                .pattern("i")
                .define('i', TCItems.INFUSED_IRON_INGOT.get())
                .define('n', TCItems.INFUSED_IRON_NUGGET.get())
                .define('p', TCBlocks.FLOW_CEDAR_PLANKS.get())
                .unlockedBy(getHasName(TCItems.INFUSED_IRON_INGOT.get()), has(TCItems.INFUSED_IRON_INGOT.get()))
                .save(pWriter);
    }

    private static ItemStack createCFJBook(int day) {
        ItemStack stack = new ItemStack(TCItems.CREATION_FLOW_JOURNAL.get());
        stack.set(TCDataComponents.BOOKMARKS,day);
        return stack;
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void cookSmoking(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_smoking");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, TerraCompositioAPI.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    protected void specialCraftingRecipe(RecipeOutput consumer, Function<CraftingBookCategory, Recipe<?>> factory, String name) {
        SpecialRecipeBuilder.special(factory).save(consumer, TerraCompositio.modLoc("dynamic/" + name).toString());
    }
}

package net.sinedkadis.terracompositio.datagen.loot;


import net.minecraft.data.loot.BlockLootSubProvider;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.enchantment.Enchantments;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.block.ModBlocks;
import org.jetbrains.annotations.NotNull;


import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.NONFLOW_LOG.get());
        this.dropSelf(ModBlocks.NONFLOW_WOOD.get());
        this.dropSelf(ModBlocks.NONFLOW_PLANKS.get());
        this.dropSelf(ModBlocks.STRIPPED_NONFLOW_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_NONFLOW_WOOD.get());
        this.dropSelf(ModBlocks.NONFLOW_PORT.get());

        this.dropSelf(ModBlocks.NONFLOW_STAIRS.get());
        this.dropSelf(ModBlocks.NONFLOW_BUTTON.get());
        this.dropSelf(ModBlocks.NONFLOW_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.NONFLOW_FENCE.get());
        this.dropSelf(ModBlocks.NONFLOW_FENCE_GATE.get());
        this.dropSelf(ModBlocks.NONFLOW_TRAPDOOR.get());
        this.dropSelf(ModBlocks.WEDGE.get());

        this.add(ModBlocks.NONFLOW_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.NONFLOW_SLAB.get()));
        this.add(ModBlocks.NONFLOW_DOOR.get(),
                block -> createDoorTable(ModBlocks.NONFLOW_DOOR.get()));


        /*this.add(ModBlocks.SAPPHIRE_ORE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.SAPPHIRE_ORE.get(), ModItems.RAW_SAPPHIRE.get()));
        this.add(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), ModItems.RAW_SAPPHIRE.get()));
        this.add(ModBlocks.NETHER_SAPPHIRE_ORE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.NETHER_SAPPHIRE_ORE.get(), ModItems.RAW_SAPPHIRE.get()));
        this.add(ModBlocks.END_STONE_SAPPHIRE_ORE.get(),
                block -> createCopperLikeOreDrops(ModBlocks.END_STONE_SAPPHIRE_ORE.get(), ModItems.RAW_SAPPHIRE.get()));
        */
        this.dropOther(ModBlocks.FLOW_LOG.get(), ModBlocks.NONFLOW_LOG.get());
        this.dropOther(ModBlocks.FLOW_WOOD.get(),ModBlocks.NONFLOW_WOOD.get());
        this.dropOther(ModBlocks.FLOW_PORT.get(),ModBlocks.NONFLOW_PORT.get());
        this.dropOther(ModBlocks.FLOW_CAULDRON.get(), Blocks.CAULDRON);
        this.dropOther(ModBlocks.BIRCH_JUICE_CAULDRON.get(), Blocks.CAULDRON);
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}

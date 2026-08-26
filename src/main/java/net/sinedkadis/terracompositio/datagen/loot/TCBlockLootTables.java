package net.sinedkadis.terracompositio.datagen.loot;


import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.fml.ModList;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.registries.TCItems;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static net.sinedkadis.terracompositio.registries.TCBlocks.*;

public class TCBlockLootTables extends BlockLootSubProvider {
    private final HolderLookup.Provider provider;

    public TCBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(),provider);
        this.provider = provider;
    }

    @Override
    protected void generate() {
        this.dropSelf(FLOW_CEDAR_LOG.get());
        this.dropSelf(FLOW_CEDAR_WOOD.get());
        this.dropSelf(FLOW_CEDAR_PLANKS.get());
        this.dropSelf(STRIPPED_FLOW_CEDAR_LOG.get());
        this.dropSelf(STRIPPED_FLOW_CEDAR_WOOD.get());
        this.dropOther(FLOW_CEDAR_ALTAR.get(), FLOW_CEDAR_LOG.get());
        this.dropSelf(CREATIVE_ECF_SOURCE.get());
        this.dropSelf(ECF_TRASH_CAN.get());

        this.dropSelf(FLOW_CEDAR_STAIRS.get());
        this.dropSelf(FLOW_CEDAR_BUTTON.get());
        this.dropSelf(FLOW_CEDAR_PRESSURE_PLATE.get());
        this.dropSelf(FLOW_CEDAR_FENCE.get());
        this.dropSelf(FLOW_CEDAR_FENCE_GATE.get());
        this.dropSelf(FLOW_CEDAR_TRAPDOOR.get());
        this.dropSelf(WEDGE.get());
        this.dropOther(FLOW_INFUSER.get(), FLOW_CEDAR_LOG.get());
        this.dropSelf(TECHNETIUM_RAW_ORE_BLOCK.get());

        this.dropSelf(FLOW_CEDAR_SAPLING.get());
        this.dropSelf(CONSTRUCTION_DESORBER.get());
        this.dropSelf(CULTIVATION_DESORBER.get());
        this.dropSelf(TIME_PASSAGE_DESORBER.get());

        this.dropSelf(FLOW_CEDAR_TANK.get());
        this.dropOther(FLOW_CEDAR_PEDESTAL.get(), FLOW_CEDAR_SAPLING.get());

        this.dropOther(FLOW_CEDAR_CASING.get(), FLOW_CEDAR_LOG.get());
        this.dropSelf(MATTER_INFUSER_PORT.get());
        this.dropSelf(MATTER_INFUSER_UNIT.get());

        this.add(FLOW_CEDAR_SLAB.get(),
                block -> createSlabItemTable(FLOW_CEDAR_SLAB.get()));
        this.add(FLOW_CEDAR_DOOR.get(),
                block -> createDoorTable(FLOW_CEDAR_DOOR.get()));

        this.dropSelf(FLOW_CEDAR_LEAVES.get());

        this.add(TECHNETIUM_ORE.get(),
                block -> createCopperLikeOreDrops(TECHNETIUM_ORE.get(), TCItems.RAW_TECHNETIUM.get()));
        this.add(TECHNETIUM_DEEPSLATE_ORE.get(),
                block -> createCopperLikeOreDrops(TECHNETIUM_DEEPSLATE_ORE.get(), TCItems.RAW_TECHNETIUM.get()));

        this.dropOther(FLOW_CEDAR_ALTAR.get(), FLOW_CEDAR_ALTAR.get());
        this.dropOther(FLOW_CAULDRON.get(), Blocks.CAULDRON);
        this.dropOther(BIRCH_JUICE_CAULDRON.get(), Blocks.CAULDRON);

        this.add(FLOW_CEDAR_SIGN.get(), block ->
                createSingleItemTable(TCItems.FLOW_CEDAR_SIGN.get()));
        this.add(FLOW_CEDAR_WALL_SIGN.get(), block ->
                createSingleItemTable(TCItems.FLOW_CEDAR_SIGN.get()));
        this.add(FLOW_CEDAR_HANGING_SIGN.get(), block ->
                createSingleItemTable(TCItems.FLOW_CEDAR_HANGING_SIGN.get()));
        this.add(FLOW_CEDAR_WALL_HANGING_SIGN.get(), block ->
                createSingleItemTable(TCItems.FLOW_CEDAR_HANGING_SIGN.get()));

        this.dropSelf(PP_COLLECTOR.get());
        this.dropSelf(PP_EMITTER.get());
        this.dropSelf(PP_RECEIVER.get());
        this.dropSelf(PP_SENDER.get());
        this.dropSelf(PP_EXTRACTOR.get());
        this.dropSelf(PP_INFUSER.get());

        this.dropSelf(TECHNETIUM_BLOCK.get());
        this.dropSelf(INFUSED_IRON_BLOCK.get());
        this.dropSelf(FLOW_CEDAR_ENT_STATUE.get());
        this.dropSelf(AIR_SATURATOR.get());

        this.dropSelf(FLOATING_REDSTONE.get());
        this.dropSelf(FLOATING_COMPARATOR.get());
        this.dropSelf(FLOATING_REPEATER.get());
        this.dropSelf(FLOATING_TORCH_HOLDER.get());
        this.dropSelf(INFUSED_IRON_PRESSURE_PLATE.get());
        this.dropSelf(INFUSED_IRON_DOOR.get());
        this.dropSelf(FLOATING_BUTTON.get());
        this.dropSelf(FLOATING_LEVER.get());

        this.dropSelf(FE_PROVIDER_CORE.get());
        this.dropSelf(FE_PROVIDER_PYLON.get());

        this.dropSelf(TIME_SATURATOR_CORE.get());

        if (ModList.get().isLoaded("create")) {
            Set<Block> blocks = new HashSet<>();
            TerraCompositio.createCompat.getDataGen().dropSelf(blocks);
            blocks.forEach(this::dropSelf);
        }
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(provider
                                        .lookupOrThrow(Registries.ENCHANTMENT)
                                        .getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}

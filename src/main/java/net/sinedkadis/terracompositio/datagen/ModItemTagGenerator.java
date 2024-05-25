package net.sinedkadis.terracompositio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ItemTags.LOGS)
                .add(ModBlocks.FLOW_LOG.get().asItem(),
                        ModBlocks.FLOW_WOOD.get().asItem());
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.NONFLOW_LOG.get().asItem(),
                        ModBlocks.NONFLOW_WOOD.get().asItem(),
                        ModBlocks.NONFLOW_PORT.get().asItem(),
                        ModBlocks.STRIPPED_NONFLOW_LOG.get().asItem(),
                        ModBlocks.STRIPPED_NONFLOW_WOOD.get().asItem());
        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.NONFLOW_PLANKS.get().asItem());
        this.tag(ItemTags.LEAVES)
                .add(ModBlocks.FLOW_LEAVES.get().asItem());
        this.tag(ItemTags.STAIRS)
                .add(ModBlocks.NONFLOW_STAIRS.get().asItem());
        this.tag(ItemTags.DOORS)
                .add(ModBlocks.NONFLOW_DOOR.get().asItem());
        this.tag(ItemTags.TRAPDOORS)
                .add(ModBlocks.NONFLOW_TRAPDOOR.get().asItem());
        this.tag(ItemTags.BUTTONS)
                .add(ModBlocks.NONFLOW_BUTTON.get().asItem());
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.NONFLOW_PRESSURE_PLATE.get().asItem());
        this.tag(ItemTags.FENCES)
                .add(ModBlocks.NONFLOW_FENCE.get().asItem());
        this.tag(ItemTags.FENCE_GATES)
                .add(ModBlocks.NONFLOW_FENCE_GATE.get().asItem());
        this.tag(ModTags.Items.NONFLOW_LOGS)
                .add(ModBlocks.NONFLOW_LOG.get().asItem(),
                        ModBlocks.NONFLOW_WOOD.get().asItem());

    }
}

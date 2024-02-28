package net.sinedkadis.terracompositio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //this.tag(ModTags.Blocks.METAL_DETECTOR_VALUABLES)
        //        .add(ModBlocks.FLOW_LOG.get()).addTag(Tags.Blocks.NEEDS_NETHERITE_TOOL);

        /*this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SAPPHIRE_BLOCK.get(),
                        ModBlocks.RAW_SAPPHIRE_BLOCK.get(),
                        ModBlocks.SAPPHIRE_ORE.get(),
                        ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                        ModBlocks.NETHER_SAPPHIRE_ORE.get(),
                        ModBlocks.END_STONE_SAPPHIRE_ORE.get(),
                        ModBlocks.SOUND_BLOCK.get());*/
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.FLOW_LOG.get(),
                        ModBlocks.FLOW_LEAVES.get(),
                        ModBlocks.FLOW_PORT.get(),
                        ModBlocks.FLOW_WOOD.get(),
                        ModBlocks.NONFLOW_LOG.get(),
                        ModBlocks.NONFLOW_WOOD.get(),
                        ModBlocks.NONFLOW_PLANKS.get(),
                        ModBlocks.STRIPPED_NONFLOW_LOG.get(),
                        ModBlocks.STRIPPED_NONFLOW_WOOD.get());


        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FLOW_LOG.get(),
                        ModBlocks.FLOW_LEAVES.get(),
                        ModBlocks.FLOW_PORT.get(),
                        ModBlocks.FLOW_WOOD.get());

        //this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
        //        .add(ModBlocks.RAW_SAPPHIRE_BLOCK.get());

        //this.tag(BlockTags.NEEDS_STONE_TOOL)
        //        .add(ModBlocks.NETHER_SAPPHIRE_ORE.get());

        //this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
        //        .add(ModBlocks.END_STONE_SAPPHIRE_ORE.get());


    }
}

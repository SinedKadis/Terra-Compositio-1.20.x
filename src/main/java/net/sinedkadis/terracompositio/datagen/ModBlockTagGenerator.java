package net.sinedkadis.terracompositio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.fluid.ModFluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
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
                        //ModBlocks.NONFLOW_PORT.get(),
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
        this.tag(BlockTags.LOGS)
                .add(ModBlocks.FLOW_LOG.get(),
                        ModBlocks.FLOW_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.NONFLOW_LOG.get(),
                        ModBlocks.NONFLOW_WOOD.get(),
                        ModBlocks.NONFLOW_PORT.get(),
                        ModBlocks.STRIPPED_NONFLOW_LOG.get(),
                        ModBlocks.STRIPPED_NONFLOW_WOOD.get());
        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.NONFLOW_PLANKS.get());
        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.FLOW_LEAVES.get());

        this.tag(BlockTags.STAIRS)
                .add(ModBlocks.NONFLOW_STAIRS.get());
        this.tag(BlockTags.DOORS)
                .add(ModBlocks.NONFLOW_DOOR.get());
        this.tag(BlockTags.TRAPDOORS)
                .add(ModBlocks.NONFLOW_TRAPDOOR.get());
        this.tag(BlockTags.BUTTONS)
                .add(ModBlocks.NONFLOW_BUTTON.get());
        this.tag(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.NONFLOW_PRESSURE_PLATE.get());
        this.tag(BlockTags.FENCES)
                .add(ModBlocks.NONFLOW_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.NONFLOW_FENCE_GATE.get());
        //this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
        //        .add(ModBlocks.RAW_SAPPHIRE_BLOCK.get());

        //this.tag(BlockTags.NEEDS_STONE_TOOL)
        //        .add(ModBlocks.NETHER_SAPPHIRE_ORE.get());

        //this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
        //        .add(ModBlocks.END_STONE_SAPPHIRE_ORE.get());

    }
}

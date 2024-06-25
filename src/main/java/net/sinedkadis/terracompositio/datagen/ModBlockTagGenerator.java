package net.sinedkadis.terracompositio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.util.ModTags;
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
                .add(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOW_CEDAR_LEAVES.get(),
                        ModBlocks.FLOWING_FLOW_PORT.get(),
                        //ModBlocks.NONFLOW_PORT.get(),
                        ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get(),
                        ModBlocks.FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOW_CEDAR_WOOD.get(),
                        ModBlocks.FLOW_CEDAR_PLANKS.get(),
                        ModBlocks.STRIPPED_FLOW_CEDAR_LOG.get(),
                        ModBlocks.STRIPPED_FLOW_CEDAR_WOOD.get());


        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOW_CEDAR_LEAVES.get(),
                        ModBlocks.FLOWING_FLOW_PORT.get(),
                        ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get());
        this.tag(BlockTags.LOGS)
                .add(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOW_CEDAR_WOOD.get(),
                        ModBlocks.FLOW_PORT.get(),
                        ModBlocks.STRIPPED_FLOW_CEDAR_LOG.get(),
                        ModBlocks.STRIPPED_FLOW_CEDAR_WOOD.get());
        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.FLOW_CEDAR_PLANKS.get());
        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.FLOW_CEDAR_LEAVES.get());

        this.tag(BlockTags.STAIRS)
                .add(ModBlocks.FLOW_CEDAR_STAIRS.get());
        this.tag(BlockTags.DOORS)
                .add(ModBlocks.FLOW_CEDAR_DOOR.get());
        this.tag(BlockTags.TRAPDOORS)
                .add(ModBlocks.FLOW_CEDAR_TRAPDOOR.get());
        this.tag(BlockTags.BUTTONS)
                .add(ModBlocks.FLOW_CEDAR_BUTTON.get());
        this.tag(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.FLOW_CEDAR_PRESSURE_PLATE.get());
        this.tag(BlockTags.FENCES)
                .add(ModBlocks.FLOW_CEDAR_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.FLOW_CEDAR_FENCE_GATE.get());
        this.tag(ModTags.Blocks.FLOW_LOGS)
                .add(ModBlocks.FLOWING_FLOW_CEDAR_LOG.get(),
                        ModBlocks.FLOWING_FLOW_CEDAR_WOOD.get(),
                        ModBlocks.FLOWING_FLOW_PORT.get());
        //this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
        //        .add(ModBlocks.RAW_SAPPHIRE_BLOCK.get());

        //this.tag(BlockTags.NEEDS_STONE_TOOL)
        //        .add(ModBlocks.NETHER_SAPPHIRE_ORE.get());

        //this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
        //        .add(ModBlocks.END_STONE_SAPPHIRE_ORE.get());

        this.tag(BlockTags.ALL_HANGING_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_HANGING_SIGN.get(),
                        ModBlocks.FLOW_CEDAR_WALL_HANGING_SIGN.get());
        this.tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_WALL_HANGING_SIGN.get());
        this.tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_HANGING_SIGN.get());
        this.tag(BlockTags.ALL_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_SIGN.get(),
                        ModBlocks.FLOW_CEDAR_WALL_SIGN.get());
        this.tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_WALL_SIGN.get());
        this.tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.FLOW_CEDAR_SIGN.get());

    }
}

package net.sinedkadis.terracompositio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.registries.TCTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.sinedkadis.terracompositio.registries.TCBlocks.*;

public class TCBlockTagGenerator extends BlockTagsProvider {
    public TCBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(TECHNETIUM_RAW_ORE_BLOCK.get(),
                        TECHNETIUM_ORE.get(),
                        TECHNETIUM_DEEPSLATE_ORE.get(),
                        MATTER_INFUSER_PORT.get(),
                        MATTER_INFUSER_UNIT.get(),
                        CONSTRUCTION_DESORBER.get(),
                        CULTIVATION_DESORBER.get(),
                        TIME_PASSAGE_DESORBER.get(),
                        PP_SENDER.get(),
                        PP_EMITTER.get(),
                        PP_COLLECTOR.get(),
                        PP_RECEIVER.get(),
                        PP_EXTRACTOR.get(),
                        PP_INFUSER.get(),
                        TECHNETIUM_BLOCK.get(),
                        INFUSED_IRON_BLOCK.get(),
                        INFUSED_IRON_DOOR.get(),
                        INFUSED_IRON_PRESSURE_PLATE.get(),
                        FLOATING_BUTTON.get(),
                        FLOATING_LEVER.get(),
                        TIME_SATURATOR_CORE.get(),
                        FE_PROVIDER_PYLON.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(FLOW_CEDAR_LOG.get(),
                        FLOW_CEDAR_LEAVES.get(),
                        FLOW_CEDAR_ALTAR.get(),
                        FLOW_INFUSER.get(),
                        FLOW_CEDAR_WOOD.get(),
                        FLOW_CEDAR_PLANKS.get(),
                        STRIPPED_FLOW_CEDAR_LOG.get(),
                        STRIPPED_FLOW_CEDAR_WOOD.get(),
                        FLOW_CEDAR_CASING.get(),
                        FLOW_CEDAR_PEDESTAL.get(),
                        FLOW_CEDAR_TANK.get(),
                        PP_SENDER.get(),
                        PP_EMITTER.get(),
                        PP_COLLECTOR.get(),
                        PP_RECEIVER.get(),
                        PP_EXTRACTOR.get(),
                        PP_INFUSER.get(),
                        FLOW_CEDAR_ENT_STATUE.get(),
                        FE_PROVIDER_CORE.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(PP_SENDER.get(),
                        PP_EMITTER.get(),
                        PP_COLLECTOR.get(),
                        PP_RECEIVER.get(),
                        PP_EXTRACTOR.get(),
                        PP_INFUSER.get());
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(FLOW_CEDAR_LOG.get(),
                        FLOW_CEDAR_WOOD.get(),
                        STRIPPED_FLOW_CEDAR_LOG.get(),
                        STRIPPED_FLOW_CEDAR_WOOD.get(),
                        FLOW_INFUSER.get(),
                        FLOW_CEDAR_CASING.get());
        this.tag(BlockTags.PLANKS)
                .add(FLOW_CEDAR_PLANKS.get());
        this.tag(BlockTags.LEAVES)
                .add(FLOW_CEDAR_LEAVES.get());

        this.tag(BlockTags.STAIRS)
                .add(FLOW_CEDAR_STAIRS.get());
        this.tag(BlockTags.DOORS)
                .add(FLOW_CEDAR_DOOR.get(),
                        INFUSED_IRON_DOOR.get());
        this.tag(BlockTags.TRAPDOORS)
                .add(FLOW_CEDAR_TRAPDOOR.get());
        this.tag(BlockTags.BUTTONS)
                .add(FLOW_CEDAR_BUTTON.get());
        this.tag(BlockTags.PRESSURE_PLATES)
                .add(FLOW_CEDAR_PRESSURE_PLATE.get(),
                        INFUSED_IRON_PRESSURE_PLATE.get());
        this.tag(BlockTags.FENCES)
                .add(FLOW_CEDAR_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(FLOW_CEDAR_FENCE_GATE.get());
        this.tag(TCTags.Blocks.FLOW_CEDAR_LOGS)
                .add(FLOW_CEDAR_LOG.get(),
                        FLOW_CEDAR_WOOD.get());

        this.tag(BlockTags.ALL_HANGING_SIGNS)
                .add(FLOW_CEDAR_HANGING_SIGN.get(),
                        FLOW_CEDAR_WALL_HANGING_SIGN.get());
        this.tag(BlockTags.WALL_HANGING_SIGNS)
                .add(FLOW_CEDAR_WALL_HANGING_SIGN.get());
        this.tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(FLOW_CEDAR_HANGING_SIGN.get());
        this.tag(BlockTags.ALL_SIGNS)
                .add(FLOW_CEDAR_SIGN.get(),
                        FLOW_CEDAR_WALL_SIGN.get());
        this.tag(BlockTags.WALL_SIGNS)
                .add(FLOW_CEDAR_WALL_SIGN.get());
        this.tag(BlockTags.STANDING_SIGNS)
                .add(FLOW_CEDAR_SIGN.get());
        this.tag(BlockTags.SAPLINGS)
                .add(FLOW_CEDAR_SAPLING.get());
        this.tag(TCTags.Blocks.REDSTONE_WIRES)
                .add(Blocks.REDSTONE_WIRE,
                        FLOATING_REDSTONE.get());


        if (ModList.get().isLoaded("create")) {
            TerraCompositio.createCompat.getDataGen().addBlockTags(this, pProvider);
        }
    }
}

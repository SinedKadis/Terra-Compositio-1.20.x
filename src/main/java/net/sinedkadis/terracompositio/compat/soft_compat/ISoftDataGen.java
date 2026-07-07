package net.sinedkadis.terracompositio.compat.soft_compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ISoftDataGen {
    void buildRecipes(RecipeProvider instance, @NotNull RecipeOutput pWriter);

    void addItemTags(ItemTagsProvider instance, HolderLookup.@NotNull Provider pProvider);

    void addBlockTags(BlockTagsProvider instance, HolderLookup.@NotNull Provider pProvider);

    void addFluidTags(FluidTagsProvider instance, HolderLookup.@NotNull Provider pProvider);

    void registerItemModels();

    void registerBlockStatesAndModels();

    void dropSelf(Set<Block> blocks);
}

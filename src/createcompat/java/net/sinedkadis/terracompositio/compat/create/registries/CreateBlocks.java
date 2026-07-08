package net.sinedkadis.terracompositio.compat.create.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sinedkadis.terracompositio.compat.create.block.custom.CedarGearboxBlock;
import net.sinedkadis.terracompositio.registries.TCBlocks;

public class CreateBlocks {
    //Compat
    public final DeferredBlock<Block> CEDAR_GEARBOX = TCBlocks.registerBlock("cedar_gearbox",
            () -> new CedarGearboxBlock(BlockBehaviour.Properties.ofFullCopy(TCBlocks.FLOW_CEDAR_LOG.get())), () -> ModList.get().isLoaded("create"));
}

package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.material.Fluid;

public abstract class TCCauldronBlock extends LayeredCauldronBlock {
    public TCCauldronBlock(Properties pProperties, Biome.Precipitation precipitation, CauldronInteraction.InteractionMap pInteractions) {
        super(precipitation, pInteractions, pProperties);
    }
    public boolean canReceiveWedgeDrip(Fluid fluid){
        return false;
    }
}

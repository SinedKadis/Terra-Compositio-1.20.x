package net.sinedkadis.terracompositio.util;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModWoodTypes {
    public static final WoodType FLOW_CEDAR = WoodType.register(new WoodType(TerraCompositio.MOD_ID + ":flow_cedar", BlockSetType.OAK));
}
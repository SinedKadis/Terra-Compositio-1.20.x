package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WedgeFluidTypes implements StringRepresentable {
    BIRCH("birch"),
    FLOW("flow"),
    NONE("none");

    private final String name;
    WedgeFluidTypes(String name){
        this.name = name;
    }
    @Override
    public String getSerializedName() {
        return this.name;
    }
}

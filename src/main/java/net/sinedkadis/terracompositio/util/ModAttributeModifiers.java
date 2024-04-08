package net.sinedkadis.terracompositio.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModAttributeModifiers {
    public static final DeferredRegister<Attribute> ATTRIBUTE_MODIFIERS = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TerraCompositio.MOD_ID);
}

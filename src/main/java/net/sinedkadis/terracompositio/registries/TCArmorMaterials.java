package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class TCArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, TerraCompositioAPI.MOD_ID);

    public static Holder<ArmorMaterial> FLOW_CEDAR = register("flow_cedar",
            new int[]{1, 3, 2, 1},
            25,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            1f,
            0f,
            () -> Ingredient.of(TCBlocks.FLOW_CEDAR_WOOD.get().asItem()));

    public static Holder<ArmorMaterial> FLOWING_FLOW_CEDAR = register("flowing_flow_cedar",
            new int[]{3, 8, 3, 2},
            25,
            BuiltInRegistries.SOUND_EVENT.getHolder(SoundEvents.BEACON_ACTIVATE.getLocation())
                    .orElse((Holder.Reference<SoundEvent>) SoundEvents.ARMOR_EQUIP_DIAMOND),
            3f,
            2f,
            () -> Ingredient.of(TCBlocks.FLOW_CEDAR_WOOD.get().asItem()));

    public static Holder<ArmorMaterial> TECHNETIUM = register("technetium",
            new int[]{5, 6, 6, 5},
            50,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5f,
            4f,
            () -> Ingredient.of(TCBlocks.TECHNETIUM_BLOCK.get()));


    private static Holder<ArmorMaterial> register(
            String name,
            int[] defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(TerraCompositio.modLoc(name)));
        return register(name, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient, list);
    }

    private static Holder<ArmorMaterial> register(
            String name,
            int[] defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers
    ) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, defense[armoritem$type.ordinal()]);
        }

        return ARMOR_MATERIALS.register(name,
                () -> new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}

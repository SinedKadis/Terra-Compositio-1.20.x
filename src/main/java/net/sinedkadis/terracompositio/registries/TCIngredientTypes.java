package net.sinedkadis.terracompositio.registries;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.components.ingredients.ExcludeItemIngredient;

public class TCIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, TerraCompositioAPI.MOD_ID);

    public static final DeferredHolder<IngredientType<?>,IngredientType<ExcludeItemIngredient>> EXCLUDE_ITEM =
            INGREDIENT_TYPES.register("exclude_item",
                    () -> new IngredientType<>(ExcludeItemIngredient.CODEC,ExcludeItemIngredient.STREAM_CODEC)
            );


    public static void register(IEventBus eventBus) {
        INGREDIENT_TYPES.register(eventBus);
    }

}

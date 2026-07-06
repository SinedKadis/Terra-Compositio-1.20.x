package net.sinedkadis.terracompositio.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;

import java.util.function.UnaryOperator;

public class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TerraCompositioAPI.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> BIND_CORDS = register("bind_cords",
            builder -> builder.persistent(BlockPos.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WrenchAxeItem.WrenchMode>> WRENCH_MODE = register("wrench_mode",
            builder -> builder
                    .persistent(WrenchAxeItem.WrenchMode.CODEC)
                    .networkSynchronized(WrenchAxeItem.WrenchMode.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CHANGE_PROPERTY = register("change_property",
            builder -> builder.persistent(Codec.STRING));


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                           UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}

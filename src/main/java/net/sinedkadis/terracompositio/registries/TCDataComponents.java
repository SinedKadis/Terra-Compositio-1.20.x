package net.sinedkadis.terracompositio.registries;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.components.FluidStackComponent;
import net.sinedkadis.terracompositio.components.ItemStackComponent;
import net.sinedkadis.terracompositio.item.custom.WrenchAxeItem;

import java.util.function.UnaryOperator;

public class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TerraCompositioAPI.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> BIND_CORDS =
            register("bind_cords",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WrenchAxeItem.WrenchMode>> WRENCH_MODE =
            register("wrench_mode",
            builder -> builder
                    .persistent(WrenchAxeItem.WrenchMode.CODEC)
                    .networkSynchronized(WrenchAxeItem.WrenchMode.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CHANGE_PROPERTY =
            register("change_property",
            builder -> builder.persistent(Codec.STRING));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> OLD_DAMAGE =
            register("old_damage",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOOKMARKS =
            register("bookmarks",
            builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidStackComponent>> FLUID =
            register("fluid_stack",
            builder -> builder
                    .persistent(FluidStackComponent.CODEC)
                    .networkSynchronized(FluidStackComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackComponent>> ECF_STORAGE_EXTENSION = register("ecf_storage_extension",
            builder -> builder
                    .persistent(ItemStackComponent.CODEC)
                    .networkSynchronized(ItemStackComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_ECF =
            register("stored_ecf",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_ECF =
            register("max_ecf",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                           UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}

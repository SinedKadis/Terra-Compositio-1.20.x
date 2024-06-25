package net.sinedkadis.terracompositio.block.entity;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TerraCompositio.MOD_ID);
    public static final RegistryObject<BlockEntityType<FlowPortBlockEntity>> FLOW_PORT_BE=
            BLOCK_ENTITIES.register("flow_port_be", ()->
                    BlockEntityType.Builder.of(FlowPortBlockEntity::new,
                            ModBlocks.FLOWING_FLOW_PORT.get()).build(null));
    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> MOD_SIGN=
            BLOCK_ENTITIES.register("mod_sign", ()->
                    BlockEntityType.Builder.of(ModSignBlockEntity::new,
                            ModBlocks.FLOW_CEDAR_SIGN.get(),ModBlocks.FLOW_CEDAR_WALL_SIGN.get()).build(null));
    public static final RegistryObject<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN=
            BLOCK_ENTITIES.register("mod_hanging_sign", ()->
                    BlockEntityType.Builder.of(ModHangingSignBlockEntity::new,
                            ModBlocks.FLOW_CEDAR_HANGING_SIGN.get(),ModBlocks.FLOW_CEDAR_WALL_HANGING_SIGN.get()).build(null));



    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

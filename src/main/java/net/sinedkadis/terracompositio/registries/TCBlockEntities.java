package net.sinedkadis.terracompositio.registries;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.entity.*;

import java.util.function.Supplier;

import static net.sinedkadis.terracompositio.registries.TCBlocks.*;

@SuppressWarnings("DataFlowIssue")
public class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TerraCompositio.MOD_ID);

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBE(String name, BlockEntityType.BlockEntitySupplier<T> blockEntity, DeferredBlock<?>... blocks) {
        return registerBE(name, blockEntity, () -> true, blocks);
    }

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBE(String name, BlockEntityType.BlockEntitySupplier<T> blockEntity, Supplier<Boolean> predicate, DeferredBlock<?>... blocks) {
        if (!predicate.get()) return null;

        return BLOCK_ENTITIES.register(name, () -> {
            Block[] oBlocks = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                var block = blocks[i];
                oBlocks[i] = block.get();
            }
            return BlockEntityType.Builder.of(blockEntity,
                    oBlocks).build(null);
        });
    }    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlowCedarAltarBlockEntity>> FLOW_ALTAR_BE =
            BLOCK_ENTITIES.register("flow_altar_be", () ->
                    BlockEntityType.Builder.of(FlowCedarAltarBlockEntity::new,
                            FLOW_CEDAR_ALTAR.get()).build(null));



    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeECFSourceBlockEntity>> CREATIVE_ECF_SOURCE_BE =
            BLOCK_ENTITIES.register("creative_ecf_source_be", () ->
                    BlockEntityType.Builder.of(CreativeECFSourceBlockEntity::new,
                            CREATIVE_ECF_SOURCE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ECFTrashCanBlockEntity>> ECF_TRASH_CAN_BE =
            BLOCK_ENTITIES.register("ecf_trash_can_be", () ->
                    BlockEntityType.Builder.of(ECFTrashCanBlockEntity::new,
                            ECF_TRASH_CAN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlowInfuserBlockEntity>> FLOW_INFUSER_BE =
            BLOCK_ENTITIES.register("flow_infuser_be", () ->
                    BlockEntityType.Builder.of(FlowInfuserBlockEntity::new,
                            FLOW_INFUSER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MatterInfuserPortBlockEntity>> MATTER_INFUSER_PORT_BE =
            BLOCK_ENTITIES.register("matter_infuser_port_be", () ->
                    BlockEntityType.Builder.of(MatterInfuserPortBlockEntity::new,
                            MATTER_INFUSER_PORT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MatterInfuserUnitBlockEntity>> MATTER_INFUSER_IO_BE =
            BLOCK_ENTITIES.register("matter_infuser_io_be", () ->
                    BlockEntityType.Builder.of(MatterInfuserUnitBlockEntity::new,
                            MATTER_INFUSER_UNIT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlowCedarCasingBlockEntity>> FLOW_CEDAR_CASING_BE =
            BLOCK_ENTITIES.register("flow_cedar_casing_io_be", () ->
                    BlockEntityType.Builder.of(FlowCedarCasingBlockEntity::new,
                            FLOW_CEDAR_CASING.get()).build(null));


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TCSignBlockEntity>> MOD_SIGN =
            BLOCK_ENTITIES.register("tc_sign", () ->
                    BlockEntityType.Builder.of(TCSignBlockEntity::new,
                            FLOW_CEDAR_SIGN.get(), FLOW_CEDAR_WALL_SIGN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TCHangingSignBlockEntity>> TC_HANGING_SIGN =
            BLOCK_ENTITIES.register("tc_hanging_sign", () ->
                    BlockEntityType.Builder.of(TCHangingSignBlockEntity::new,
                            FLOW_CEDAR_HANGING_SIGN.get(), FLOW_CEDAR_WALL_HANGING_SIGN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConstructionDesorberBlockEntity>> CONSTRUCTION_DESORBER_BE =
            BLOCK_ENTITIES.register("construction_desorber_be", () ->
                    BlockEntityType.Builder.of(ConstructionDesorberBlockEntity::new,
                            CONSTRUCTION_DESORBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CultivationDesorberBlockEntity>> CULTIVATION_DESORBER_BE =
            BLOCK_ENTITIES.register("cultivation_desorber_be", () ->
                    BlockEntityType.Builder.of(CultivationDesorberBlockEntity::new,
                            CULTIVATION_DESORBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TimePassageDesorberBlockEntity>> TIME_PASSAGE_DESORBER_BE =
            BLOCK_ENTITIES.register("time_passage_desorber_be", () ->
                    BlockEntityType.Builder.of(TimePassageDesorberBlockEntity::new,
                            TIME_PASSAGE_DESORBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlowCedarTankBlockEntity>> FLOW_CEDAR_TANK_BE =
            BLOCK_ENTITIES.register("flow_cedar_tank_be", () ->
                    BlockEntityType.Builder.of(FlowCedarTankBlockEntity::new,
                            FLOW_CEDAR_TANK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PathPointerBlockEntity>> PATH_POINTER_BE =
            registerBE("path_pointer_be",PathPointerBlockEntity::new,
                    PP_COLLECTOR,
                    PP_EMITTER,
                    PP_RECEIVER,
                    PP_SENDER,
                    PP_EXTRACTOR,
                    PP_INFUSER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntStatueBlockEntity>> ENT_STATUE_BE =
            BLOCK_ENTITIES.register("ent_statue_be", () ->
                    BlockEntityType.Builder.of(EntStatueBlockEntity::new,
                            FLOW_CEDAR_ENT_STATUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AirSaturatorBlockEntity>> AIR_SATURATOR_BE =
            registerBE("air_saturator_be",AirSaturatorBlockEntity::new, AIR_SATURATOR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FloatingComparatorBlockEntity>> FLOATING_COMPARATOR_BE =
            registerBE("floating_comparator", FloatingComparatorBlockEntity::new, FLOATING_COMPARATOR);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FEProviderCoreBlockEntity>> FE_PROVIDER_CORE_BE =
            registerBE("fe_provider_core_be", FEProviderCoreBlockEntity::new, FE_PROVIDER_CORE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FEProviderPylonBlockEntity>> FE_PROVIDER_PYLON_BE =
            registerBE("fe_provider_pylon_be", FEProviderPylonBlockEntity::new, FE_PROVIDER_PYLON);



    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

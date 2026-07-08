package net.sinedkadis.terracompositio.compat.create.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.compat.create.TCCreateCompat;
import net.sinedkadis.terracompositio.compat.create.block.entity.CedarGearboxBlockEntity;

import static net.sinedkadis.terracompositio.registries.TCBlockEntities.registerBE;

public class CreateBlockEntities {
    public final DeferredHolder<BlockEntityType<?>,BlockEntityType<CedarGearboxBlockEntity>> CEDAR_GEARBOX_BE =
            registerBE("cedar_gearbox_be", CedarGearboxBlockEntity::new, () -> ModList.get().isLoaded("create"), ((TCCreateCompat) TerraCompositio.createCompat).blocks.CEDAR_GEARBOX);
}

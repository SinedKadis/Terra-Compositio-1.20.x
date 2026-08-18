package net.sinedkadis.terracompositio.block.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.registries.TCBlockEntities;

@MethodsReturnNonnullByDefault
public class TimeSaturatorCoreBlock extends TCBaseEntityBlock{
    public TimeSaturatorCoreBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntityType<? extends TCBlockEntity> getBlockEntityType() {
        return TCBlockEntities.TIME_SATURATOR_CORE_BE.get();
    }
}

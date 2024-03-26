package net.sinedkadis.terracompositio.fluid;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sinedkadis.terracompositio.TerraCompositio;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, TerraCompositio.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,TerraCompositio.MOD_ID);
    public static final ModFluidRegistryContainer FLOW_FLUID =new ModFluidRegistryContainer("flow",
            FluidType.Properties.create().canDrown(true).canSwim(true).supportsBoating(true).canPushEntity(true),
            ()-> ModFluidRegistryContainer.createExtension(new ModFluidRegistryContainer.ClientExtensions(TerraCompositio.MOD_ID,"flow")/*.still("flow")*/.renderOverlay(null).tint(0x1e8dc6).fogColor(0.30f,1.41f,1.98f)),
            new ModFluidRegistryContainer.AdditionalProperties().levelDecreasePerBlock(1).slopeFindDistance(4),
            BlockBehaviour.Properties.copy(Blocks.WATER),
            new Item.Properties().stacksTo(1));
}

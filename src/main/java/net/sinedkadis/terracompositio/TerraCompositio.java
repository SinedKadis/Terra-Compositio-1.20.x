package net.sinedkadis.terracompositio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.compat.patchouli.TCPatchouliCompat;
import net.sinedkadis.terracompositio.compat.soft_compat.ISoftCompat;
import net.sinedkadis.terracompositio.config.TCClientConfigs;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.config.TCServerConfigs;
import net.sinedkadis.terracompositio.ecf.ECFNetworkHandler;
import net.sinedkadis.terracompositio.events.ECFNetworkEvent;
import net.sinedkadis.terracompositio.events.FluidNetworkEvent;
import net.sinedkadis.terracompositio.fluid.FluidNetworkHandler;
import net.sinedkadis.terracompositio.registries.*;
import net.sinedkadis.terracompositio.worldgen.biome.TCTerrablender;
import net.sinedkadis.terracompositio.worldgen.tree.TCFoliagePlacers;
import net.sinedkadis.terracompositio.worldgen.tree.TCTrunkPlacers;

//6011371823902440939 - cool seed(for 1.20.1)
@Mod(TerraCompositio.MOD_ID)
public class TerraCompositio
{
    public static final String MOD_ID = TerraCompositioAPI.MOD_ID;
    public static ResourceLocation modLoc(String location){
        return ResourceLocation.tryBuild(MOD_ID,location);
    }

    public static ISoftCompat createCompat;

    public TerraCompositio(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        //NeoForge.EVENT_BUS.register(this);
        TCFluids.FLUIDS.register(modEventBus);
        TCFluids.FLUID_TYPES.register(modEventBus);

        TCCreativeModeTabs.register(modEventBus);
        TCItems.register(modEventBus);
        TCBlocks.register(modEventBus);
        TCParticles.register(modEventBus);
        TCEffects.register(modEventBus);
        TCPotions.register(modEventBus);
        TCSounds.register(modEventBus);
        TCEntities.register(modEventBus);
        TCBlockEntities.register(modEventBus);
        TCRecipes.register(modEventBus);
        TCTrunkPlacers.register(modEventBus);
        TCFoliagePlacers.register(modEventBus);
        TCTerrablender.registerBiomes();

        TCDataComponents.register(modEventBus);
        TCIngredientTypes.register(modEventBus);
        TCAttachments.register(modEventBus);

        TCGameRules.init();

        if (ModList.get().isLoaded("create")) {
            try {
                Class<?> clazz = Class.forName("net.sinedkadis.terracompositio.compat.create.TCCreateCompat");
                ISoftCompat compat = (ISoftCompat) clazz.getDeclaredConstructor().newInstance();
                createCompat = compat;
                compat.init();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load Create compat", e);
            }
        }

        modContainer.registerConfig(ModConfig.Type.CLIENT, TCClientConfigs.SPEC, "terracompositio-client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, TCCommonConfigs.SPEC, "terracompositio-common.toml");
        modContainer.registerConfig(ModConfig.Type.SERVER, TCServerConfigs.SPEC, "terracompositio-server.toml");



    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener((ECFNetworkEvent e) -> ECFNetworkHandler.INSTANCE.onNetworkEvent(e.getSource(),e.getAction()));
        bus.addListener((FluidNetworkEvent e) -> FluidNetworkHandler.INSTANCE.onNetworkEvent(e.getSource(),e.getAction()));
        if (ModList.get().isLoaded("patchouli"))
            TCPatchouliCompat.registerMultiblocks();

        if (ModList.get().isLoaded("create"))
            createCompat.commonInit();
        //SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, TCSurfaceRules.makeRules());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Items.BUNDLE);
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(TCItems.FLOW_CEDAR_ENT_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(TCBlocks.FLOATING_BUTTON);
            event.accept(TCBlocks.FLOATING_LEVER);
            event.accept(TCBlocks.FLOATING_REDSTONE);
            event.accept(TCBlocks.FLOATING_REPEATER);
            event.accept(TCBlocks.FLOATING_COMPARATOR);
            event.accept(TCBlocks.FLOATING_TORCH_HOLDER);
            event.accept(TCBlocks.INFUSED_IRON_DOOR);
            event.accept(TCBlocks.INFUSED_IRON_PRESSURE_PLATE);
        }
    }

}

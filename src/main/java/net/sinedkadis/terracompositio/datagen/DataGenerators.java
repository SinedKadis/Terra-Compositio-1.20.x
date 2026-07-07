package net.sinedkadis.terracompositio.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.sinedkadis.terracompositio.TerraCompositio;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new TCRecipeProvider(packOutput,event.getLookupProvider()));
        generator.addProvider(event.includeServer(), TCLootTableProvider.create(packOutput,event.getLookupProvider()));

        generator.addProvider(event.includeClient(), new TCBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new TCItemModelProvider(packOutput, existingFileHelper));

        TCBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new TCBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new TCItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(),new TCFluidTagGenerator(packOutput,lookupProvider,existingFileHelper));
        generator.addProvider(event.includeServer(), new TCWorldGenProvider(packOutput, lookupProvider));
    }
}

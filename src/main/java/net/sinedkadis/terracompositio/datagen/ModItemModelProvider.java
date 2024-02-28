package net.sinedkadis.terracompositio.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TerraCompositio.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.PEBBLE);
        simpleItem(ModItems.STONE_STAFF);





        // Генеруємо модель для предмету, коли тримається в руці
        singleTexture("stone_staff",
                mcLoc("item/handheld"),
                "layer0",
                modLoc("item/stone_staff3d"));

        // Генеруємо стандартну модель предмету для інвентаря
        withExistingParent("stone_staff",
                modLoc("item/stone_staff2d"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(TerraCompositio.MOD_ID,"item/" + item.getId().getPath()));
    }

}

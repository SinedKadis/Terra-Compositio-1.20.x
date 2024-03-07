package net.sinedkadis.terracompositio.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TerraCompositio.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logBlockWithItem(ModBlocks.FLOW_LOG);
        blockWithItem(ModBlocks.FLOW_PORT);
        blockWithItem(ModBlocks.FLOW_LEAVES);
        blockWithItem(ModBlocks.NONFLOW_PORT);
        woodBlockWithItem(ModBlocks.FLOW_WOOD,ModBlocks.FLOW_LOG);
        logBlockWithItem(ModBlocks.NONFLOW_LOG);
        blockWithItem(ModBlocks.NONFLOW_PLANKS);
        woodBlockWithItem(ModBlocks.NONFLOW_WOOD,ModBlocks.NONFLOW_LOG);
        logBlockWithItem(ModBlocks.STRIPPED_NONFLOW_LOG);
        woodBlockWithItem(ModBlocks.STRIPPED_NONFLOW_WOOD,ModBlocks.STRIPPED_NONFLOW_LOG);

        stairsBlock(((StairBlock) ModBlocks.NONFLOW_STAIRS.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        slabBlock(((SlabBlock) ModBlocks.NONFLOW_SLAB.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.NONFLOW_BUTTON.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.NONFLOW_PRESSURE_PLATE.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        fenceBlock(((FenceBlock) ModBlocks.NONFLOW_FENCE.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.NONFLOW_FENCE_GATE.get()),blockTexture(ModBlocks.NONFLOW_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.NONFLOW_DOOR.get()),modLoc("block/nonflow_door_bottom"),modLoc("block/nonflow_door_top"),"cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.NONFLOW_TRAPDOOR.get()),modLoc("block/nonflow_trapdoor"),true,"cutout");


    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private  void woodBlockWithItem(RegistryObject<Block> block, RegistryObject<Block> texture){
        axisBlock(((RotatedPillarBlock) block.get()),blockTexture(texture.get()),blockTexture(texture.get()));
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(TerraCompositio.MOD_ID+":block/"+ ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }
    private void logBlockWithItem(RegistryObject<Block> block){
        logBlock((RotatedPillarBlock) block.get());
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(TerraCompositio.MOD_ID+":block/"+ ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

}

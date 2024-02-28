package net.sinedkadis.terracompositio.datagen;


import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TerraCompositio.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        rotatedPillarBlockWithItem(ModBlocks.FLOW_LOG);
        rotatedPillarBlockWithItem(ModBlocks.FLOW_PORT);
        blockWithItem(ModBlocks.FLOW_LEAVES);

        rotatedBlockWithItem(ModBlocks.FLOW_WOOD);
        rotatedPillarBlockWithItem(ModBlocks.NONFLOW_LOG);
        blockWithItem(ModBlocks.NONFLOW_PLANKS);
        rotatedBlockWithItem(ModBlocks.NONFLOW_WOOD);
        rotatedPillarBlockWithItem(ModBlocks.STRIPPED_NONFLOW_LOG);
        rotatedBlockWithItem(ModBlocks.STRIPPED_NONFLOW_WOOD);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
    private void rotatedPillarBlockWithItem(RegistryObject<Block> block){

        ModelFile vertical = models().cubeColumn(block.getId().getPath(),
                modLoc("block/"+block.getId().getPath()),
                modLoc("block/"+block.getId().getPath()+"_top"));
        ModelFile horizontal = models().cubeColumnHorizontal(block.getId().getPath(),
                modLoc("block/"+block.getId().getPath()),
                modLoc("block/"+block.getId().getPath()+"_top"));
        getVariantBuilder(block.get())
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .modelForState().modelFile(vertical).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .modelForState().modelFile(horizontal).rotationX(90).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
        simpleBlockItem(block.get(),models().cubeColumn(block.getId().getPath(),
                modLoc("block/"+block.getId().getPath()),
                modLoc("block/"+block.getId().getPath()+"_top")));

    }
    private void rotatedBlockWithItem(RegistryObject<Block> block){
        ModelFile vertical = models().cubeColumn(block.getId().getPath(),
                modLoc("block/"+block.getId().getPath()),
                modLoc("block/"+block.getId().getPath()));
        ModelFile horizontal = models().cubeColumnHorizontal(block.getId().getPath(),
                modLoc("block/"+block.getId().getPath()),
                modLoc("block/"+block.getId().getPath()));
        getVariantBuilder(block.get())
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .modelForState().modelFile(vertical).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .modelForState().modelFile(horizontal).rotationX(90).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
        simpleBlockItem(block.get(),models().cubeColumn(block.getId().getPath(),
                        modLoc("block/"+block.getId().getPath()),
                        modLoc("block/"+block.getId().getPath())));
    }

}

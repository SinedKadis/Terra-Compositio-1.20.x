package net.sinedkadis.terracompositio.block.behaviours;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;

import java.util.List;

public class AssemblyBehaviour implements IBEBehaviour, IHaveKnowledge, TooltipHelper.ICustomKey {
    public static int CHECK_RATE = 60;
    protected final TCBlockEntity blockEntity;
    public static TooltipHelper.ICustomKey ASSEMBLY = new AssemblyBehaviour(null);

    public AssemblyBehaviour(TCBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public boolean allowCrafting;

    protected boolean isAssembled(ServerLevel level) {
        return true;
    }

    @Override
    public void onSave(CompoundTag compoundTag, HolderLookup.Provider registries) {

    }

    @Override
    public void onLoad(CompoundTag compoundTag, HolderLookup.Provider registries) {

    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        if (!allowCrafting)
            data.putBoolean("disassembled", true);
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.CRAFTING, tooltip, t -> {
            if (!data.contains(TooltipHelper.Keys.CRAFT_EXCEPTION.toData()) && data.contains("disassembled")) {
                TooltipHelper.addWithNoArg(
                        TooltipHelper.Keys.CRAFT_EXCEPTION,
                        ITCRecipe.CraftException.NO_SURROUNDINGS,
                        t
                );
            }
        });
    }

    @Override
    public void tick() {
        Level level = blockEntity.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (level.getGameTime() % CHECK_RATE == 0) {
            allowCrafting = isAssembled(serverLevel);
            blockEntity.getPersistentData().putBoolean(ASSEMBLY.toData(), allowCrafting);
        }
    }

    @Override
    public String name() {
        return "ASSEMBLED";
    }

    @Override
    public String getModID() {
        return TerraCompositioAPI.MOD_ID;
    }

}

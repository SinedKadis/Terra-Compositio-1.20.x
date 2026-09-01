package net.sinedkadis.terracompositio.block.behaviours;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.sinedkadis.terracompositio.api.IHaveKnowledge;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.config.TCCommonConfigs;
import net.sinedkadis.terracompositio.recipe.ITCRecipe;
import net.sinedkadis.terracompositio.recipe.ITCRecipeType;
import net.sinedkadis.terracompositio.util.behaviors.blockentity.IBEBehaviour;

import java.util.List;
import java.util.Optional;

public class CraftingBehaviour<INPUT extends RecipeInput, RECIPE extends ITCRecipe<INPUT>> implements IBEBehaviour, IHaveKnowledge {
    private final TCBlockEntity blockEntity;
    private final ITCRecipeType<INPUT, RECIPE> type;

    protected RECIPE cachedRecipe;

    protected int progress = 0;
    protected ITCRecipe.CraftException craftException = ITCRecipe.CraftException.OK;


    public CraftingBehaviour(TCBlockEntity blockEntity, ITCRecipeType<INPUT, RECIPE> type) {
        this.blockEntity = blockEntity;
        this.type = type;
    }

    @Override
    public void tick() {
        craftException = hasRecipe();

        CompoundTag persistentData = blockEntity.getPersistentData();
        boolean isAssembled = !persistentData.contains(AssemblyBehaviour.ASSEMBLY.toData()) || persistentData.getBoolean(AssemblyBehaviour.ASSEMBLY.toData());
        if (cachedRecipe != null && isAssembled) {
            ++progress;
            cachedRecipe.onCraftingTick(blockEntity, progress);
            if (cachedRecipe.isCompleteThenCraft(blockEntity, progress)) {
                progress = 0;
                cachedRecipe = null;
            }
        } else {
            progress = 0;
        }
    }

    @Override
    public void onSave(CompoundTag compoundTag, HolderLookup.Provider registries) {
        compoundTag.putInt(TooltipHelper.Keys.PROGRESS.toData(), progress);
    }

    @Override
    public void onLoad(CompoundTag compoundTag, HolderLookup.Provider registries) {
        progress = compoundTag.getInt(TooltipHelper.Keys.PROGRESS.toData());
    }

    public ITCRecipe.CraftException hasRecipe() {
        Optional<RecipeHolder<RECIPE>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return ITCRecipe.CraftException.NO_RECIPE;
        }
        RECIPE value = recipe.get().value();
        ITCRecipe.CraftException canBeProcessed = value.allowOnTick(blockEntity);
        if (!canBeProcessed.hasExceptions()) {
            cachedRecipe = value;
        } else {
            cachedRecipe = null;
        }
        return canBeProcessed;
    }

    protected Optional<RecipeHolder<RECIPE>> getCurrentRecipe() {
        Level level = blockEntity.getLevel();
        assert level != null;
        return level.getRecipeManager().getRecipeFor(type, type.getRecipeInput(blockEntity), level);
    }

    @Override
    public void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider) {
        if (!craftException.equals(ITCRecipe.CraftException.OK) && !craftException.equals(ITCRecipe.CraftException.NO_RECIPE)) {
            data.putString(TooltipHelper.Keys.CRAFT_EXCEPTION.toData(), craftException.name());
        } else if (craftException.equals(ITCRecipe.CraftException.OK)) {
            data.putInt(TooltipHelper.Keys.PROGRESS.toData(), progress);
            if (cachedRecipe != null)
                cachedRecipe.collectKnowledgeData(data, provider);
        }
    }

    @Override
    public void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider) {
        TooltipHelper.addWithHeader(TooltipHelper.Headers.CRAFTING, tooltip, t -> {
            if (cachedRecipe != null)
                cachedRecipe.addTooltipLines(data, t, isShifting, provider);

            if (TCCommonConfigs.DEBUG.get())
                TooltipHelper.addIfExist(TooltipHelper.Keys.PROGRESS, TooltipHelper.Units.UNITS, t, data);
            if (data.contains(TooltipHelper.Keys.MAX_PROGRESS.toData())) {
                int max = data.getInt(TooltipHelper.Keys.MAX_PROGRESS.toData());
                if (data.contains(TooltipHelper.Keys.PROGRESS.toData())) {
                    int cur = data.getInt(TooltipHelper.Keys.PROGRESS.toData());
                    if (isShifting)
                        t.add(TooltipHelper.keyWithArg(TooltipHelper.Keys.TIME_REMAINING, (max - cur) / 20, TooltipHelper.Units.SECONDS));
                    else
                        TooltipHelper.addScale(TooltipHelper.Keys.PROGRESS, cur, max, t, ChatFormatting.GREEN);
                }
                if (data.contains(TooltipHelper.Keys.ECF_CONSUME.toData())) {
                    int consume = data.getInt(TooltipHelper.Keys.ECF_CONSUME.toData());
                    t.add(TooltipHelper.keyWithArg(TooltipHelper.Keys.CONSUME, ((float) consume) / max / 20, TooltipHelper.Units.ECF_SECOND));
                }
            }

            if (data.contains(TooltipHelper.Keys.CRAFT_EXCEPTION.toData())) {
                TooltipHelper.addWithNoArg(
                        TooltipHelper.Keys.CRAFT_EXCEPTION,
                        Enum.valueOf(ITCRecipe.CraftException.class, data.getString(TooltipHelper.Keys.CRAFT_EXCEPTION.toData())),
                        t
                );
            }
        });
    }
}

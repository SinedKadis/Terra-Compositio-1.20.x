package net.sinedkadis.terracompositio.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.sinedkadis.terracompositio.api.helpers.TooltipHelper;
import net.sinedkadis.terracompositio.api.tooltip.HeaderComponent;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implement that to add info on Knowledge Overlay. Works on {@link net.minecraft.world.level.block.entity.BlockEntity}
 * and {@link net.minecraft.world.entity.Entity}
 */
public interface IHaveKnowledge {

    /**
     * Collect knowledge data. Use {@link TooltipHelper.ICustomKey#toData()} to easily get key to store value.
     * Add all the data that you want to send from server to client
     *
     * @param data     the data to store
     * @param provider the registries
     */
    void collectKnowledgeData(CompoundTag data, HolderLookup.Provider provider);

    /**
     * Adds tooltip lines on client, taking info from data.
     * Use {@link TooltipHelper#addIfExist(TooltipHelper.ICustomKey, TooltipHelper.Units, List, CompoundTag)} to easily display data.
     * You can put components from {@link net.sinedkadis.terracompositio.api.tooltip} to tooltip,
     * but for {@link HeaderComponent} use {@link TooltipHelper#addWithHeader(TooltipHelper.ICustomHeader, List, Consumer)}
     *
     * @param data       the data
     * @param tooltip    the tooltip
     * @param isShifting is player shifting
     * @param provider the registries
     */
    void addTooltipLines(CompoundTag data, List<Component> tooltip, boolean isShifting, HolderLookup.Provider provider);
}

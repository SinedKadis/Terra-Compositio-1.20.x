package net.sinedkadis.terracompositio.components.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.sinedkadis.terracompositio.registries.TCIngredientTypes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Getter
public class ExcludeItemIngredient implements ICustomIngredient {

    public static MapCodec<ExcludeItemIngredient> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("wrapped")
                            .forGetter(ExcludeItemIngredient::getWrapped),
                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("excluded")
                            .forGetter(ExcludeItemIngredient::getExcluded)
            ).apply(instance,ExcludeItemIngredient::new));

    public static StreamCodec<RegistryFriendlyByteBuf,ExcludeItemIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,ExcludeItemIngredient::getWrapped,
            ByteBufCodecs.holderRegistry(Registries.ITEM),ExcludeItemIngredient::getExcluded,
            ExcludeItemIngredient::new
    );

    private final Ingredient wrapped;
    private final Holder<Item> excluded;
    private final NonNullList<ItemStack> itemStacksWithoutExcluded;

    public ExcludeItemIngredient(Ingredient wrapped, Holder<Item> excluded) {
        this.wrapped = wrapped;
        this.excluded = excluded;
        ItemStack[] items = wrapped.getItems();
        ItemStack[] itemStacks = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (!itemStack.is(excluded)) {
                itemStacks[i] = itemStack;
            } else {
                itemStacks[i] = ItemStack.EMPTY;
            }
        }
        this.itemStacksWithoutExcluded = NonNullList.of(ItemStack.EMPTY, itemStacks);

    }

    @Override
    public boolean test(ItemStack stack) {
        return wrapped.test(stack) && !stack.is(excluded);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return itemStacksWithoutExcluded.stream();
    }

    @Override
    public boolean isSimple() {
        return wrapped.isSimple();
    }

    @Override
    public IngredientType<?> getType() {
        return TCIngredientTypes.EXCLUDE_ITEM.get();
    }
}

package net.sinedkadis.terracompositio.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ItemStackComponent(ItemStack itemStack) {
    public static Codec<ItemStackComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("item_stack").forGetter(ItemStackComponent::itemStack)
            ).apply(instance, ItemStackComponent::new)
    );
    public static StreamCodec<RegistryFriendlyByteBuf, ItemStackComponent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ItemStackComponent::itemStack, ItemStackComponent::new
    );
}

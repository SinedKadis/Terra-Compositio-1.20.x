package net.sinedkadis.terracompositio.api.tooltip;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * The implementation of Component that holds {@link FluidStack}. Processed in Knowledge Overlay.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record FluidComponent(FluidStack fluidStack) implements Component {

    /**
     * Factory method for {@link FluidComponent}
     *
     * @param fluidStack the fluid stack
     * @return the fluid component
     */
    public static FluidComponent of(FluidStack fluidStack) {
        return new FluidComponent(fluidStack);
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }

    @Override
    public ComponentContents getContents() {
        throw new RuntimeException("Content of fluid component read unexpected");
    }

    @Override
    public List<Component> getSiblings() {
        return List.of();
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return FormattedCharSequence.EMPTY;
    }

}

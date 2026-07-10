package net.sinedkadis.terracompositio.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

@Mixin(CreateJEI.class)
public class CreateJeiMixin {
    @Inject(
            method = "registerRecipes(Lmezz/jei/api/registration/IRecipeRegistration;)V",
            at = @At("TAIL")
    )
    private void injectAdditionalRecipes(IRecipeRegistration registration, CallbackInfo ci) {
        registration.addRecipes(createRecipeHolderType(Create.asResource("item_application")),
                List.of(new RecipeHolder<>(TerraCompositio.modLoc("cedar_tank_upgrade_axe"), new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new, TerraCompositio.modLoc("cedar_tank_2"))
                                .require(TCBlocks.FLOW_CEDAR_TANK_3.get())
                                .require(ItemTags.AXES)
                                .output(TCBlocks.FLOW_CEDAR_TANK_2.get())
                                .build()),
                        new RecipeHolder<>(TerraCompositio.modLoc("cedar_sapling_to_pedestal"), new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new,
                                TerraCompositio.modLoc("cedar_pedestal"))
                                .require(TCBlocks.FLOW_CEDAR_SAPLING.get())
                                .require(Items.BONE_MEAL)
                                .output(TCBlocks.FLOW_CEDAR_PEDESTAL.get())
                                .build()))
        );
    }

}

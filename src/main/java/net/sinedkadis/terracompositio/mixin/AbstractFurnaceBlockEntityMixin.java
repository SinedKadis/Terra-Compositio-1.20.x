package net.sinedkadis.terracompositio.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sinedkadis.terracompositio.recipe.TechnetiumFiringRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.sinedkadis.terracompositio.entity.custom.ECFCloudEntity.placeECFCloud;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements Container {

    @Inject(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getTotalCookTime(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)I",
                    shift = At.Shift.AFTER
            )
    )
    private static void beforeSetRecipeUsed(
            Level pLevel,
            BlockPos pPos,
            BlockState pState,
            AbstractFurnaceBlockEntity pBlockEntity,
            CallbackInfo ci
    ) {
        Optional<RecipeHolder<TechnetiumFiringRecipe>> firingRecipe = pLevel.getRecipeManager()
                .getRecipeFor(
                        TechnetiumFiringRecipe.Type.INSTANCE,
                        new SingleRecipeInput(pBlockEntity.getItem(0)),
                        pLevel
                );

        if (firingRecipe.isPresent()) {
            int ecf = firingRecipe.get().value().getEcf();
            placeECFCloud(pLevel, pPos, ecf);
        }
    }
}

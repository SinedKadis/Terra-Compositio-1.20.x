package net.sinedkadis.terracompositio.compat.create.recipes;

import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import net.minecraft.world.item.crafting.Ingredient;

public class ManualApplicationWIthToolRecipe extends ManualApplicationRecipe {
    public ManualApplicationWIthToolRecipe(ItemApplicationRecipeParams params) {
        super(params);
    }

    public Ingredient getToolHeldItem() {
        if (ingredients.size() < 3)
            throw new IllegalStateException("Item Application Recipe: " + getType() + " has no offhand tool!");
        return ingredients.get(2);
    }
}

package me.kenzierocks.plugins.tbm.recipe;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.item.inventory.ItemStack;

public interface Recipe {

    // There's no good way to do this...
    /**
     * Attempts to apply this recipe to the given items + layout.
     * 
     * @param asLayout
     *            - The items as placed in the grid, useful for shaped recipes.
     *            This 2D-array is guaranteed to have no extra space on the top
     *            or left.
     * @param asList
     *            - The items as a single list, useful for shapeless recipes.
     * @return The result item, if present.
     */
    Optional<ItemStack> tryToApplyRecipe(ItemStack[][] asLayout,
            List<ItemStack> asList);

}
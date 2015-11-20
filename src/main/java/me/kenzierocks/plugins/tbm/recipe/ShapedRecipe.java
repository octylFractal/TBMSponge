package me.kenzierocks.plugins.tbm.recipe;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.spongepowered.api.item.inventory.ItemStack;

public interface ShapedRecipe extends Recipe {

    int getRows();

    int getCols();

    ItemStack getStackAt(int r, int c);
    
    ItemStack translateLayoutToOutput(ItemStack[][] asLayout);

    /**
     * Check if the given ItemStack matches at the given row and column.
     * 
     * @param r
     *            - row
     * @param c
     *            - column
     * @return {@code true} if it matches, otherwise {@code false}
     */
    default boolean matches(ItemStack stack, int r, int c) {
        return stack.equals(getStackAt(r, c));
    }

    @Override
    default Optional<ItemStack> tryToApplyRecipe(ItemStack[][] asLayout,
            List<ItemStack> asList) {
        checkState(getRows() > 0, "cannot have 0 rows");
        checkState(getCols() > 0, "cannot have 0 columns");
        if (asLayout.length <= getRows() || asLayout.length < 1) {
            return Optional.empty();
        }
        if (asLayout[0].length <= getCols()) {
            return Optional.empty();
        }
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStack recipe = getStackAt(r, c);
                ItemStack user = asLayout[r][c];
                if (!Objects.equals(recipe, user)) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(translateLayoutToOutput(asLayout));
    }

    interface SingleOutput extends ShapedRecipe {

        ItemStack getOutput();
        
        @Override
        default ItemStack translateLayoutToOutput(ItemStack[][] asLayout) {
            return getOutput();
        }

    }

}
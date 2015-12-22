package me.kenzierocks.plugins.tbm.recipe;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public interface ShapedRecipe extends Recipe {

    int getRows();

    int getCols();

    ItemStack getStackAt(int r, int c);

    ItemStack translateLayoutToOutput(ItemStack[][] asLayout);

    /**
     * Check if the given ItemStack matches at the given row and column. Any
     * stacks that contain {@link ItemTypes#NONE} are equivalent to {@code null}
     * , e.g. if {@code stack} is {@code null}, and the recipe has a stack of
     * type {@code NONE} at the position, then this method returns {@code true}.
     * 
     * @param r
     *            - row
     * @param c
     *            - column
     * @return {@code true} if it matches, otherwise {@code false}
     */
    default boolean matches(ItemStack stack, int r, int c) {
        if (stack == null) {
            // special case
            return getStackAt(r, c).getItem().equals(ItemTypes.NONE);
        }
        return ItemStackComparators.ALL.compare(stack, getStackAt(r, c)) == 0;
    }

    @Override
    default Optional<ItemStack> tryToApplyRecipe(ItemStack[][] asLayout,
            List<ItemStack> asList) {
        checkState(getRows() > 0, "cannot have 0 rows");
        checkState(getCols() > 0, "cannot have 0 columns");
        if (asLayout.length < getRows() || asLayout.length < 1) {
            return Optional.empty();
        }
        if (asLayout[0].length < getCols()) {
            return Optional.empty();
        }
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStack user = asLayout[r][c];
                if (!matches(user, r, c)) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(translateLayoutToOutput(asLayout));
    }

    interface SingleOutput extends ShapedRecipe {

        ItemStackSnapshot getOutput();

        @Override
        default ItemStack translateLayoutToOutput(ItemStack[][] asLayout) {
            return getOutput().createStack();
        }

    }

}
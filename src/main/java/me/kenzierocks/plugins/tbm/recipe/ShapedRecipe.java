package me.kenzierocks.plugins.tbm.recipe;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import me.kenzierocks.plugins.tbm.TBMDataManager;

public interface ShapedRecipe extends Recipe {

    int getRows();

    int getCols();

    ItemStack getStackAt(int r, int c);

    ItemStack translateLayoutToOutput(ItemStack[][] asLayout);

    /**
     * Check if the given ItemStack matches at the given row and column. Any
     * stacks that contain {@link ItemTypes#NONE} are equivalent to {@code null}
     * , i.e. if {@code stack} is {@code null}, and the recipe has a stack of
     * type {@code NONE} at the position, then this method returns {@code true}.
     * Item count is ignored.
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
        return TBMDataManager.itemStacksEqualIgnoringSize(stack,
                getStackAt(r, c));
    }

    @Override
    default Optional<ItemStack> tryToApplyRecipe(CraftingData data) {
        checkState(getRows() > 0, "cannot have 0 rows");
        checkState(getCols() > 0, "cannot have 0 columns");
        if (data.getAsLayout().length < getRows()
                || data.getAsLayout().length < 1) {
            return Optional.empty();
        }
        if (data.getAsLayout()[0].length < getCols()) {
            return Optional.empty();
        }
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStack user = data.getAsLayout()[r][c];
                if (!matches(user, r, c)) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(translateLayoutToOutput(data.getAsLayout()));
    }

    @Override
    default Optional<CraftingData> onResultTaken(CraftingData data,
            ItemStack takenResult,
            Function<ItemStack, ItemStack> getContainerItem) {
        // First, double check that the recipe matches
        return tryToApplyRecipe(data).map(res -> {
            // Next, double check the amounts
            int possible = res.getQuantity();
            int taken = takenResult.getQuantity();
            if (possible < taken) {
                // Too many taken - take none
                return null;
            }
            // Next, apply the recipe repeatedly
            int done = 0;
            CraftingData next = data;
            while (true) {
                try {
                    next = removeItemsForOneApply(next, getContainerItem);
                    done++;
                } catch (IllegalArgumentException cannotApply) {
                    break;
                }
            }
            if (done != taken) {
                // bahasdasd?
                return null;
            }
            return next;
        });
    }

    @Override
    default CraftingData removeItemsForOneApply(CraftingData data,
            Function<ItemStack, ItemStack> getContainerItem) {
        ItemStack[][] layout = data.getAsLayout();
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStack recipeStack = getStackAt(r, c).copy();
                ItemStack layoutStack = layout[r][c];
                ItemStack containerItem = getContainerItem.apply(layoutStack);
                if (containerItem != null) {
                    int q = layoutStack.getQuantity();
                    layoutStack = containerItem.copy();
                    layoutStack.setQuantity(q);
                }
                if (!TBMDataManager.itemStacksEqualIgnoringSize(recipeStack,
                        layoutStack)
                        || (layoutStack.getQuantity()
                                - recipeStack.getQuantity()) < 0) {
                    throw new IllegalArgumentException("Cannot apply once");
                }
                while (recipeStack.getQuantity() > 0
                        && layoutStack.getQuantity() > 0) {
                    layoutStack.setQuantity(layoutStack.getQuantity() - 1);
                    recipeStack.setQuantity(recipeStack.getQuantity() - 1);
                }
            }
        }
        return data.withLayout(layout);
    }

    interface SingleOutput extends ShapedRecipe {

        ItemStackSnapshot getOutput();

        @Override
        default ItemStack translateLayoutToOutput(ItemStack[][] asLayout) {
            return getOutput().createStack();
        }

    }

}
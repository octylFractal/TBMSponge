package me.kenzierocks.plugins.tbm.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

import me.kenzierocks.plugins.tbm.TBMPlugin;

/**
 * Delicious.
 */
public class RecipeManager {

    private final List<Recipe> recipes = new ArrayList<>();

    @Listener
    public void onChangeInventory(InteractInventoryEvent event) {
        Logger logger = TBMPlugin.getInstance().getLogger();
        // logger.info("I AM CHANGE INV " + event);
        Inventory parent = event.getTargetInventory();
        // logger.info("Here's the type hierarchy");
        Class<?> current = parent.getClass();
        // recursiveDetail(logger, current, 0);
        if (parent instanceof CraftingInventory) {
            CraftingInventory table = (CraftingInventory) parent;
            checkRecipe(table);
        }
    }

    private void recursiveDetail(Logger logger, Class<?> current, int depth) {
        if (current == null) {
            return;
        }
        String indent = Strings.repeat("  ", depth);
        logger.info(indent + current.getName());
        recursiveIfaceDetail(logger, current, depth + 1);
        recursiveDetail(logger, current.getSuperclass(), depth + 1);
    }

    private void recursiveIfaceDetail(Logger logger, Class<?> iface,
            int depth) {
        // don't print out the iface name
        String indent = Strings.repeat("  ", depth);
        for (Class<?> inner : iface.getInterfaces()) {
            logger.info(indent + "|I|" + inner.getName());
            recursiveIfaceDetail(logger, inner, depth + 1);
        }
    }

    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }

    private void checkRecipe(CraftingInventory table) {
        ItemStack[][] asLayout = toLayout(table);
        List<ItemStack> asList = toList(table);
        for (Recipe recipe : this.recipes) {
            Optional<ItemStack> result =
                    recipe.tryToApplyRecipe(asLayout, asList);
            if (result.isPresent()) {
                table.getResult().set(result.get());
                break;
            }
        }
    }

    private ItemStack[][] toLayout(CraftingInventory table) {
        GridInventory grid = table.getCraftingGrid();
        ItemStack[][] stacks = new ItemStack[grid.getRows()][grid.getColumns()];
        int[] rowCounts = new int[grid.getRows()];
        int[] colCounts = new int[grid.getColumns()];
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getColumns(); c++) {
                ItemStack stack = grid.getSlot(r, c).get().peek();
                if (!stack.getItem().equals(ItemTypes.NONE)) {
                    stacks[r][c] = stack;
                    rowCounts[r]++;
                    colCounts[c]++;
                }
            }
        }
        // re-adjust stacks to fill top/left
        int firstNonEmptyRow = -1;
        for (int r = 0; r < rowCounts.length; r++) {
            if (rowCounts[r] != 0) {
                firstNonEmptyRow = r;
                break;
            }
        }
        int firstNonEmptyCol = -1;
        for (int c = 0; c < colCounts.length; c++) {
            if (colCounts[c] != 0) {
                firstNonEmptyCol = c;
                break;
            }
        }
        if (firstNonEmptyRow != 0) {
            stacks = Arrays.copyOfRange(stacks, firstNonEmptyRow,
                    stacks.length);
        }
        if (firstNonEmptyCol != 0) {
            int nonEmptyCol = firstNonEmptyCol;
            stacks = Stream
                    .of(stacks).map(oldRow -> Arrays.copyOfRange(oldRow,
                            nonEmptyCol, oldRow.length))
                    .toArray(ItemStack[][]::new);
        }
        return stacks;
    }

    private List<ItemStack> toList(CraftingInventory table) {
        return FluentIterable.from(table.getCraftingGrid())
                .transform(slot -> slot.peek())
                .filter(stack -> !stack.getItem().equals(ItemTypes.NONE))
                .toList();
    }

}

package me.kenzierocks.plugins.tbm.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.google.common.collect.FluentIterable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.common.ForgeHooks;

/**
 * Delicious.
 */
public class RecipeManager {

    private final List<Recipe> recipes = new ArrayList<>();

    @Listener
    public void onChangeInventory(ChangeInventoryEvent event,
            @First Player player) {
        Inventory parent = event.getTargetInventory();
        if (parent instanceof ContainerWorkbench) {
            ContainerWorkbench table = (ContainerWorkbench) parent;
            if (event.getTransactions().stream().anyMatch(
                    t -> ((Slot) t.getSlot()).inventory == table.craftMatrix)) {
                checkRecipeASE(table.craftMatrix, table.craftResult);
                afterCheckRecipe(table.windowId, table.craftResult, player);
            }
            if (event.getTransactions().stream().anyMatch(
                    t -> ((Slot) t.getSlot()).inventory == table.craftResult)) {
                SlotTransaction slotT = event.getTransactions().stream()
                        .filter(t -> ((Slot) t
                                .getSlot()).inventory == table.craftResult)
                        .reduce(null, (x, y) -> {
                            if (x != null && y != null) {
                                throw new IllegalStateException("lol");
                            }
                            return com.google.common.base.Objects
                                    .firstNonNull(x, y);
                        });
                produceResult(toLayoutASE(table.craftMatrix),
                        toListASE(table.craftMatrix)).ifPresent(x -> {
                            if (slotT.getFinal().getType()
                                    .equals(ItemTypes.NONE)
                                    && ItemStackComparators.ALL.compare(
                                            slotT.getOriginal().createStack(),
                                            x) == 0) {
                                // extract of recipe!
                                clearTheTable(table, table.craftMatrix, player);
                            }
                        });
            }
        }
        if (parent instanceof ContainerPlayer) {
            ContainerPlayer table = (ContainerPlayer) parent;
            if (event.getTransactions().stream().anyMatch(
                    t -> ((Slot) t.getSlot()).inventory == table.craftMatrix)) {
                checkRecipeASE(table.craftMatrix, table.craftResult);
                afterCheckRecipe(table.windowId, table.craftResult, player);
            }
            if (event.getTransactions().stream().anyMatch(
                    t -> ((Slot) t.getSlot()).inventory == table.craftResult)) {
                SlotTransaction slotT = event.getTransactions().stream()
                        .filter(t -> ((Slot) t
                                .getSlot()).inventory == table.craftResult)
                        .reduce(null, (x, y) -> {
                            if (x != null && y != null) {
                                throw new IllegalStateException("lol");
                            }
                            return com.google.common.base.Objects
                                    .firstNonNull(x, y);
                        });
                produceResult(toLayoutASE(table.craftMatrix),
                        toListASE(table.craftMatrix)).ifPresent(x -> {
                            if (slotT.getFinal().getType()
                                    .equals(ItemTypes.NONE)
                                    && ItemStackComparators.ALL.compare(
                                            slotT.getOriginal().createStack(),
                                            x) == 0) {
                                System.err.println("est extractus");
                                // extract of recipe!
                                clearTheTable(table, table.craftMatrix, player);
                            }
                        });
            }
        }
    }

    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }

    // API SUCKS EDITION

    private void clearTheTable(Container c, InventoryCrafting table,
            Player player) {
        for (int i = 0; i < table.getSizeInventory(); i++) {
            net.minecraft.item.ItemStack inSlot = table.getStackInSlot(i);
            if (inSlot != null) {
                table.setInventorySlotContents(i,
                        ForgeHooks.getContainerItem(inSlot));
            }
        }
        for (Object o : c.inventorySlots) {
            ((EntityPlayerMP) player).playerNetServerHandler
                    .sendPacket(new S2FPacketSetSlot(c.windowId,
                            ((Slot) o).slotNumber, ((Slot) o).getStack()));
        }
    }

    private void afterCheckRecipe(int windowId, IInventory craftResult,
            Player player) {
        ((EntityPlayerMP) player).playerNetServerHandler
                .sendPacket(new S2FPacketSetSlot(windowId, 0,
                        craftResult.getStackInSlot(0)));
    }

    private void checkRecipeASE(InventoryCrafting table,
            IInventory craftResult) {
        ItemStack[][] asLayout = toLayoutASE(table);
        List<ItemStack> asList = toListASE(table);
        produceResult(asLayout, asList).ifPresent(x -> craftResult
                .setInventorySlotContents(0, (net.minecraft.item.ItemStack) x));
    }

    private ItemStack[][] toLayoutASE(InventoryCrafting table) {
        ItemStack[][] stacks =
                new ItemStack[table.getHeight()][table.getWidth()];
        int[] rowCounts = new int[table.getHeight()];
        int[] colCounts = new int[table.getWidth()];
        for (int r = 0; r < table.getHeight(); r++) {
            for (int c = 0; c < table.getWidth(); c++) {
                ItemStack stack =
                        (ItemStack) table.getStackInRowAndColumn(r, c);
                if (stack != null && !stack.getItem().equals(ItemTypes.NONE)) {
                    stacks[c][r] = stack;
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
        if (firstNonEmptyRow == -1) {
            stacks = new ItemStack[0][0];
        } else if (firstNonEmptyRow != 0) {
            stacks = Arrays.copyOfRange(stacks, firstNonEmptyRow,
                    stacks.length);
        }
        if (firstNonEmptyCol == -1) {
            stacks = new ItemStack[0][0];
        } else if (firstNonEmptyCol != 0) {
            int nonEmptyCol = firstNonEmptyCol;
            stacks = Stream
                    .of(stacks).map(oldRow -> Arrays.copyOfRange(oldRow,
                            nonEmptyCol, oldRow.length))
                    .toArray(ItemStack[][]::new);
        }
        return stacks;
    }

    private List<ItemStack> toListASE(IInventory table) {
        return IntStream.range(0, table.getSizeInventory())
                .mapToObj(table::getStackInSlot).filter(Objects::nonNull)
                .map(ItemStack.class::cast).collect(Collectors.toList());
    }

    // END ASE

    private void checkRecipe(CraftingInventory table) {
        ItemStack[][] asLayout = toLayout(table);
        List<ItemStack> asList = toList(table);
        produceResult(asLayout, asList).ifPresent(table.getResult()::set);
    }

    private Optional<ItemStack> produceResult(ItemStack[][] asLayout,
            List<ItemStack> asList) {
        return this.recipes.stream()
                .map(x -> x.tryToApplyRecipe(asLayout, asList))
                .filter(Optional::isPresent).findFirst()
                .orElseGet(Optional::empty);
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

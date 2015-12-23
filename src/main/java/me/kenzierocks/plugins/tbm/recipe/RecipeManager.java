package me.kenzierocks.plugins.tbm.recipe;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

import me.kenzierocks.plugins.tbm.Shortcuts;
import me.kenzierocks.plugins.tbm.TBMPlugin;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Delicious.
 */
public class RecipeManager {

    private final List<Recipe> recipes = new ArrayList<>();

    @Listener
    public void onChangeInventory(ChangeInventoryEvent event,
            @First Player player) {
        if (event.getCause().first(Player.class).isPresent()) {
            // disable stuff
            Inventory parent = event.getTargetInventory();
            resendInv((Container) parent, player);
            if (parent instanceof ContainerWorkbench) {
                resendInv((Container) parent, player);
            }
            if (parent instanceof ContainerPlayer) {
                resendInv((Container) parent, player);
            }
            return;
        }
        Inventory parent = event.getTargetInventory();
        if (parent instanceof ContainerWorkbench) {
            doEverythingJustDoIt(event.getTransactions(),
                    (ContainerWorkbench) parent, player);
        }
        if (parent instanceof ContainerPlayer) {
            doEverythingJustDoIt(event.getTransactions(),
                    (ContainerPlayer) parent, player);
        }
    }

    private void resendInv(Container container, Player player) {
        // This sets all the slots properly :3
        ((EntityPlayerMP) player).sendContainerToPlayer(container);
//        for (Object o : container.inventorySlots) {
//            System.err.println("Sending slot " + ((Slot) o).slotNumber + " = "
//                    + ((Slot) o).getStack());
//            ((EntityPlayerMP) player).playerNetServerHandler
//                    .sendPacket(new S2FPacketSetSlot(container.windowId,
//                            ((Slot) o).slotNumber, ((Slot) o).getStack()));
//        }
    }

    private void doEverythingJustDoIt(List<SlotTransaction> transactions,
            Container table, Player player) {
        // craft-in-progress
        // if (transactions.stream().anyMatch(
        // t -> ((Slot) t.getSlot()).inventory == getCraftMatrix(table))) {
        // checkRecipeASE(getCraftMatrix(table), getCraftResult(table));
        // afterCheckRecipe(table.windowId, getCraftResult(table), player);
        // }
        // take crafted stuff
        if (transactions.stream().anyMatch(
                t -> ((Slot) t.getSlot()).inventory == getCraftResult(table))) {
            List<SlotTransaction> filtered = transactions.stream()
                    .filter(t -> ((Slot) t
                            .getSlot()).inventory == getCraftResult(table))
                    .collect(Collectors.toList());
            checkState(filtered.size() == 1, "shrug.jpg");
            SlotTransaction slotT = filtered.get(0);
            CraftingData data =
                    new CraftingData(toLayoutASE(getCraftMatrix(table)),
                            toListASE(getCraftMatrix(table)));
            produceResult(data).ifPresent(x -> {
                if (slotT.getFinal().getType().equals(ItemTypes.NONE)
                        && ItemStackComparators.ALL.compare(
                                slotT.getOriginal().createStack(), x) == 0) {
                    // extract of recipe!
                    clearTheTable(table, player, slotT.getFinal().createStack(),
                            data);
                }
            });
        }
    }

    private InventoryCrafting getCraftMatrix(Container c) {
        if (c instanceof ContainerWorkbench) {
            ContainerWorkbench bench = (ContainerWorkbench) c;
            return bench.craftMatrix;
        } else if (c instanceof ContainerPlayer) {
            ContainerPlayer player = (ContainerPlayer) c;
            return player.craftMatrix;
        } else if (c == null) {
            throw new NullPointerException("c");
        }
        throw new IllegalArgumentException(
                "no support for " + c.getClass().getName());
    }

    private IInventory getCraftResult(Container c) {
        if (c instanceof ContainerWorkbench) {
            ContainerWorkbench bench = (ContainerWorkbench) c;
            return bench.craftResult;
        } else if (c instanceof ContainerPlayer) {
            ContainerPlayer player = (ContainerPlayer) c;
            return player.craftResult;
        } else if (c == null) {
            throw new NullPointerException("c");
        }
        throw new IllegalArgumentException(
                "no support for " + c.getClass().getName());
    }

    public void addRecipe(Recipe recipe) {
        if (recipe instanceof DefaultShapedRecipe) {
            DefaultShapedRecipe dsr = (DefaultShapedRecipe) recipe;
            GameRegistry.addRecipe(new IRecipe() {

                @Override
                public boolean matches(InventoryCrafting inv, World worldIn) {
                    return dsr.tryToApplyRecipe(
                            new CraftingData(toLayoutASE(inv), toListASE(inv)))
                            .isPresent();
                }

                @Override
                public net.minecraft.item.ItemStack[]
                        getRemainingItems(InventoryCrafting inv) {
                    net.minecraft.item.ItemStack[] aitemstack =
                            new net.minecraft.item.ItemStack[inv
                                    .getSizeInventory()];

                    for (int i = 0; i < aitemstack.length; ++i) {
                        net.minecraft.item.ItemStack itemstack =
                                inv.getStackInSlot(i);
                        aitemstack[i] = ForgeHooks.getContainerItem(itemstack);
                    }

                    return aitemstack;
                }

                @Override
                public int getRecipeSize() {
                    return dsr.getCols() * dsr.getRows();
                }

                @Override
                public net.minecraft.item.ItemStack getRecipeOutput() {
                    return (net.minecraft.item.ItemStack) dsr.getOutput()
                            .createStack();
                }

                @SuppressWarnings("unchecked")
                @Override
                public net.minecraft.item.ItemStack
                        getCraftingResult(InventoryCrafting inv) {
                    // haha i shouldn't do this :3
                    TBMPlugin.getInstance().getExecutor().schedule(() -> {
                        try {
                            Field eventHandler = InventoryCrafting.class
                                    .getDeclaredFields()[3];
                            eventHandler.setAccessible(true);
                            Container container =
                                    (Container) eventHandler.get(inv);
                            Field[] f = Container.class.getDeclaredFields();
                            List<ICrafting> crafterList = null;
                            for (int i = 1; i < f.length; i++) {
                                f[i].setAccessible(true);
                                if (f[i].getType() == List.class
                                        && f[i - 1].getType() == Set.class) {
                                    crafterList = (List<ICrafting>) f[i]
                                            .get(container);
                                }
                            }
                            for (ICrafting craft : crafterList) {
                                if (craft instanceof Player) {
                                    Player p = (Player) craft;
                                    resendInv(container, p);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 10, TimeUnit.MILLISECONDS);
                    return (net.minecraft.item.ItemStack) dsr.tryToApplyRecipe(
                            new CraftingData(toLayoutASE(inv), toListASE(inv)))
                            .orElse(null);
                }
            });
        }
        this.recipes.add(recipe);
    }

    private boolean clearTheTable(Container container, Player player,
            ItemStack taken, CraftingData data) {
        InventoryCrafting craftMatrix = getCraftMatrix(container);
        boolean cancel = true;
        for (Recipe recipe : this.recipes) {
            Optional<ItemStack> result = recipe.tryToApplyRecipe(data);
            if (result.isPresent()) {
                Optional<CraftingData> endResult = recipe.onResultTaken(data,
                        taken, x -> (ItemStack) ForgeHooks.getContainerItem(
                                (net.minecraft.item.ItemStack) x));
                if (endResult.isPresent()) {
                    cancel = false;
                    ItemStack[][] layout = endResult.get().getAsLayout(false);
                    for (int c = 0; c < layout.length; c++) {
                        for (int r = 0; r < layout[0].length; r++) {
                            craftMatrix.setInventorySlotContents(
                                    r + c * craftMatrix.getWidth(),
                                    (net.minecraft.item.ItemStack) layout[r][c]);
                        }
                    }
                    break;
                }
            }
        }
        // This sets all the slots properly :3
        for (Object o : container.inventorySlots) {
            ((EntityPlayerMP) player).playerNetServerHandler
                    .sendPacket(new S2FPacketSetSlot(container.windowId,
                            ((Slot) o).slotNumber, ((Slot) o).getStack()));
        }
        return cancel;
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
        produceResult(new CraftingData(asLayout, asList))
                .ifPresent(x -> craftResult.setInventorySlotContents(0,
                        (net.minecraft.item.ItemStack) x));
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

    private void checkRecipe(CraftingInventory table) {
        ItemStack[][] asLayout = toLayout(table);
        List<ItemStack> asList = toList(table);
        produceResult(new CraftingData(asLayout, asList))
                .ifPresent(table.getResult()::set);
    }

    private Optional<ItemStack> produceResult(CraftingData data) {
        return this.recipes.stream().map(x -> x.tryToApplyRecipe(data))
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

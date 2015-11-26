package me.kenzierocks.plugins.tbm;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;

public class EventHandler {

    private final TBMPlugin plugin;

    public EventHandler(TBMPlugin tbmPlugin) {
        this.plugin = tbmPlugin;
    }

    @Listener
    public void onPlaceBlock(ChangeBlockEvent.Place place) {
        Optional<?> rootCause = place.getCause().root();
        rootCause.filter(ArmorEquipable.class::isInstance)
                .map(ArmorEquipable.class::cast)
                .ifPresent(e -> onEquipablePlaceBlock(place, e));
    }

    private void onEquipablePlaceBlock(ChangeBlockEvent.Place event,
            ArmorEquipable armEq) {
        Optional<ItemStack> usedOpt = armEq.getItemInHand();
        if (!usedOpt.isPresent()) {
            // placed without a stack, can't know what was used...
            return;
        }
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
        checkState(transactions.size() == 1, "too much stuff happened.");
        Transaction<BlockSnapshot> t = transactions.get(0);
        if (!t.getOriginal().getState().getType().equals(BlockTypes.AIR)) {
            // Must be a From-Air transfer, otherwise our cancel will break
            // things
            return;
        }
        ItemStack used = usedOpt.get();
        for (String part : System.getProperty("java.class.path")
                .split(Pattern.quote(File.pathSeparator))) {
            this.plugin.getLogger().info(part);
        }
        if (DataManager.isTBMBlockStack(used)) {
            // convert the block to an entity, spawn it, cancel the event.
            Entity blockEntity = DataManager.covertBlock(t.getFinal());
            event.getTargetWorld().spawnEntity(blockEntity,
                    Cause.of(new Object() {

                        @Override
                        public String toString() {
                            return "NONE OF MY CAUSES ARE BUILDABLE ;_;";
                        }
                    }));
            event.setCancelled(true);
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
    }

}

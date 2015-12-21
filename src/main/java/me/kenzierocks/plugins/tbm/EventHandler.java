package me.kenzierocks.plugins.tbm;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;

public class EventHandler {

    private final TBMPlugin plugin;

    public EventHandler(TBMPlugin tbmPlugin) {
        this.plugin = tbmPlugin;
    }

    @Listener
    public void onPlaceBlock(ChangeBlockEvent.Place event,
            @Root ArmorEquipable armEq) {
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
        if (TBMDataManager.isTBMBlockStack(used)) {
            // convert the block to an entity, spawn it, cancel the event.
            Entity blockEntity = TBMDataManager.covertBlock(t.getFinal());
            // uh sure. correlation implies causation.
            event.getTargetWorld().spawnEntity(blockEntity, Cause.of(
                    this.plugin,
                    SpawnCause.builder().type(SpawnTypes.PLUGIN).build()));
            event.setCancelled(true);
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
    }

}

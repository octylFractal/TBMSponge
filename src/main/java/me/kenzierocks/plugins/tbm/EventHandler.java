package me.kenzierocks.plugins.tbm;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class EventHandler {

    private final TBMPlugin plugin;

    public EventHandler(TBMPlugin tbmPlugin) {
        this.plugin = tbmPlugin;
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
        BlockSnapshot target = event.getTargetBlock();
        Entity tbmEntity = DataManager.covertBlock(target);
    }

}

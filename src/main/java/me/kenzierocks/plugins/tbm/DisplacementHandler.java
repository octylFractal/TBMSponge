package me.kenzierocks.plugins.tbm;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;

public class DisplacementHandler {

    // blah blah blah, no thread-safety needed here
    /**
     * true when moving FallingBlocks manually and the event shouldn't be
     * cancelled.
     */
    public static boolean manualMovement = false;

    private final TBMPlugin plugin;

    public DisplacementHandler(TBMPlugin tbmPlugin) {
        this.plugin = tbmPlugin;
    }

    @Listener
    public void onEntityMove(DisplaceEntityEvent.Move event) {
        Entity ent = event.getTargetEntity();
        if (ent instanceof FallingBlock) {
            this.plugin.getLogger().info(event.getFromTransform().toString());
            this.plugin.getLogger().info(event.getToTransform().toString());
        }
    }

}

package me.kenzierocks.plugins.tbm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

/**
 * Because these things are really long.
 */
public final class Shortcuts {

    public static ItemStack.Builder itemStackBuilder() {
        return Sponge.getRegistry().createBuilder(ItemStack.Builder.class);
    }

    // a cache for storing itemtype -> itemstack mappings
    private static final Map<ItemType, ItemStackSnapshot> ofTypeCache =
            new HashMap<>();

    public static ItemStack singleStackOfItem(ItemType type) {
        return ofTypeCache
                .computeIfAbsent(type,
                        t -> ItemStack.of(type, 1).createSnapshot())
                .createStack();
    }

    public static Logger log() {
        return TBMPlugin.getInstance().getLogger();
    }

    private Shortcuts() {
    }

}

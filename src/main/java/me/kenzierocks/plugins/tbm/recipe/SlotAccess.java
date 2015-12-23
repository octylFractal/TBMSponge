package me.kenzierocks.plugins.tbm.recipe;

import org.spongepowered.api.item.inventory.ItemStack;

public interface SlotAccess {

     ItemStack getSlot(int x, int y);

     void setSlot(int x, int y, ItemStack item);
     
     void removeStack(ItemStack stack);

}

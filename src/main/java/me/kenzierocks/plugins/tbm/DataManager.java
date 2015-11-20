package me.kenzierocks.plugins.tbm;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

public class DataManager {

    private static final ItemStack WAND_THING_STACK;

    static {
        GameRegistry registry = TBMPlugin.getInstance().getGame().getRegistry();
        EnchantmentData enchanting = registry.getManipulatorRegistry()
                .getBuilder(EnchantmentData.class).get().create()
                .set(Keys.ITEM_ENCHANTMENTS, ImmutableList
                        .of(new ItemEnchantment(Enchantments.FORTUNE, 100)));
        WAND_THING_STACK = registry.createBuilder(ItemStack.Builder.class)
                .itemType(ItemTypes.GOLDEN_PICKAXE).itemData(enchanting)
                .quantity(1).build();
    }

    public static ItemStack createWandThingStack() {
        return WAND_THING_STACK.copy();
    }
    
    public static boolean isWandThingStack(ItemStack stack) {
        return WAND_THING_STACK.equals(stack);
    }

    public static boolean isTBMEntity(Entity entity) {
        return entity.get(TBMKeys.IS_TBM_ENTITY).orElse(Boolean.FALSE);
    }

    public static DataTransactionResult setTBMEntity(Entity entity) {
        return entity.offer(TBMKeys.IS_TBM_ENTITY, Boolean.TRUE);
    }

    public static Entity covertBlock(BlockSnapshot target) {
        // TODO Auto-generated method stub
        return null;
    }

}

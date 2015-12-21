package me.kenzierocks.plugins.tbm;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.common.data.manipulator.mutable.item.SpongeEnchantmentData;

import com.google.common.collect.ImmutableList;

import me.kenzierocks.plugins.tbm.TBMKeys.TBMTaggedData;

public class TBMDataManager {

    private static final ItemStack WAND_THING_STACK;
    private static final ItemStack DRILL_STACK;
    private static final ItemStack CPU_STACK;
    private static final ItemStack ENGINE_STACK;
    private static final ItemStack CARGO_STACK;
    private static final ItemStack EJECTOR_STACK;
    private static final ItemStack FILLER_STACK;

    static {
        // Generate data
        DataManager dataManager = Sponge.getDataManager();
        TBMTaggedData tbmData = dataManager.getBuilder(TBMTaggedData.class)
                .get().build(new MemoryDataContainer().set(TBMKeys.TBM_TAGGED,
                        Boolean.TRUE))
                .get();
        // Build stacks
        Supplier<ItemStack.Builder> builder = () -> Sponge.getRegistry()
                .createBuilder(ItemStack.Builder.class);
        EnchantmentData enchanting = new SpongeEnchantmentData()
                .set(Keys.ITEM_ENCHANTMENTS, ImmutableList
                        .of(new ItemEnchantment(Enchantments.FORTUNE, 100)));
        // dataManager.getManipulatorBuilder(EnchantmentData.class)
        // .get()
        // .build(new MemoryDataContainer()
        // .set(Keys.ITEM_ENCHANTMENTS,
        // ImmutableList.of(new ItemEnchantment(
        // Enchantments.FORTUNE, 100))))
        // .get();
        Function<ItemStack.Builder, ItemStack.Builder> tbmify =
                b -> b.itemData(tbmData);
        WAND_THING_STACK =
                tbmify.apply(builder.get().itemType(ItemTypes.GOLDEN_PICKAXE)
                        .itemData(enchanting).quantity(1)).build();
        DRILL_STACK = tbmify
                .apply(builder.get().itemType(ItemTypes.DISPENSER).quantity(1))
                .build();
        CPU_STACK = tbmify.apply(
                builder.get().itemType(ItemTypes.REDSTONE_LAMP).quantity(1))
                .build();
        ENGINE_STACK = tbmify
                .apply(builder.get().itemType(ItemTypes.DISPENSER).quantity(1))
                .build();
        CARGO_STACK = tbmify
                .apply(builder.get().itemType(ItemTypes.DISPENSER).quantity(1))
                .build();
        EJECTOR_STACK = tbmify
                .apply(builder.get().itemType(ItemTypes.DROPPER).quantity(1))
                .build();
        FILLER_STACK = tbmify.apply(
                builder.get().itemType(ItemTypes.CRAFTING_TABLE).quantity(1))
                .build();
    }

    private static boolean itemStacksEqualIgnoringSize(ItemStack a,
            ItemStack b) {
        checkNotNull(a);
        checkNotNull(b);
        ItemStack aZero = a.copy();
        aZero.setQuantity(0);
        ItemStack bZero = b.copy();
        bZero.setQuantity(0);
        // Should work decently.
        return aZero.equals(bZero);
    }

    public static ItemStack getWandThingStack() {
        return WAND_THING_STACK.copy();
    }

    public static boolean isWandThingStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(WAND_THING_STACK, stack);
    }

    public static ItemStack getCargoStack() {
        return CARGO_STACK.copy();
    }

    public static boolean isCargoStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(CARGO_STACK, stack);
    }

    public static ItemStack getCpuStack() {
        return CPU_STACK.copy();
    }

    public static boolean isCpuStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(CPU_STACK, stack);
    }

    public static ItemStack getDrillStack() {
        return DRILL_STACK.copy();
    }

    public static boolean isDrillStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(DRILL_STACK, stack);
    }

    public static ItemStack getEjectorStack() {
        return EJECTOR_STACK.copy();
    }

    public static boolean isEjectorStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(EJECTOR_STACK, stack);
    }

    public static ItemStack getEngineStack() {
        return ENGINE_STACK.copy();
    }

    public static boolean isEngineStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(ENGINE_STACK, stack);
    }

    public static ItemStack getFillerStack() {
        return FILLER_STACK.copy();
    }

    public static boolean isFillerStack(ItemStack stack) {
        return itemStacksEqualIgnoringSize(FILLER_STACK, stack);
    }

    /**
     * Returns true for any stack that is one of the BLOCK stacks (i.e. not the
     * wand).
     * 
     * @param stack
     * @return
     */
    public static boolean isTBMBlockStack(ItemStack stack) {
        return isTBMTagged(stack) && !isWandThingStack(stack);
    }

    public static boolean isTBMTagged(DataHolder data) {
        return data.get(TBMKeys.TBM_TAGGED).orElse(Boolean.FALSE);
    }

    public static DataTransactionResult addTBMTag(DataHolder data) {
        return data.offer(TBMKeys.TBM_TAGGED, Boolean.TRUE);
    }

    public static Entity covertBlock(BlockSnapshot target) {
        // Create the ArmorStand the block rides
        ArmorStand armorStand =
                createCarrierArmorStand(target.getLocation().get());
        FallingBlock block = createBlockEntity(target);
        armorStand.offer(Keys.PASSENGER, block);
        // TODO: define if this returns the actual block or the stand
        return block;
    }

    private static ArmorStand createCarrierArmorStand(Location<?> location) {
        ArmorStand base = (ArmorStand) location.getExtent()
                .createEntity(EntityTypes.ARMOR_STAND,
                        location.getBlockPosition())
                .orElseThrow(() -> new IllegalArgumentException(
                        "WTF? Cannot create for location" + location));
        base.setGravity(false);
        base.offer(Keys.INVISIBLE, Boolean.TRUE);
        addTBMTag(base);
        return base;
    }

    private static FallingBlock createBlockEntity(BlockSnapshot target) {
        Location<?> location = target.getLocation().get();
        FallingBlock block = (FallingBlock) location.getExtent()
                .createEntity(EntityTypes.FALLING_BLOCK,
                        location.getBlockPosition())
                .orElseThrow(() -> new IllegalArgumentException(
                        "WTF? Cannot create for location" + location));
        block.offer(Keys.FALLING_BLOCK_STATE, target.getState());
        block.offer(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, Boolean.FALSE);
        block.offer(Keys.FALL_TIME, Integer.valueOf(1));
        addTBMTag(block);
        return block;
    }

}

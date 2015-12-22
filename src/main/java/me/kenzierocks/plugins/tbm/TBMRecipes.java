package me.kenzierocks.plugins.tbm;

import org.spongepowered.api.item.ItemTypes;

import me.kenzierocks.plugins.tbm.recipe.DefaultShapedRecipe;
import me.kenzierocks.plugins.tbm.recipe.RecipeManager;

public final class TBMRecipes {

    static void initRecipes(RecipeManager manager) {
        Shortcuts.log().info("Loading recipes");
        // Cargo
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getCargoStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('c', Shortcuts.singleStackOfItem(ItemTypes.CHEST))
                .link('m', Shortcuts.singleStackOfItem(ItemTypes.MINECART))
                .row1('i', 'i', 'i').row2('i', 'c', 'i').row3('i', 'm', 'i')
                .build());
        // CPU
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getCpuStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('r', Shortcuts.singleStackOfItem(ItemTypes.REDSTONE))
                .link('d', Shortcuts.singleStackOfItem(ItemTypes.DIAMOND))
                .row1('i', 'i', 'i').row2('i', 'r', 'i').row3('i', 'd', 'i')
                .build());
        // Drill
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getDrillStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('d', Shortcuts.singleStackOfItem(ItemTypes.DIAMOND))
                .row1(' ', 'd', ' ').row2('i', 'i', 'i').row3('i', 'i', 'i')
                .build());
        // Ejector
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getEjectorStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('j', Shortcuts.singleStackOfItem(ItemTypes.JUKEBOX))
                .row1('i', 'i', 'i').row2('i', 'j', 'i').row3('i', 'i', 'i')
                .build());
        // Engine
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getEngineStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('f', Shortcuts.singleStackOfItem(ItemTypes.FURNACE))
                .link('m', Shortcuts.singleStackOfItem(ItemTypes.MINECART))
                .row1('i', 'i', 'i').row2('i', 'f', 'i').row3('i', 'm', 'i')
                .build());
        // Filler
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getFillerStack())
                        .link('i',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.IRON_INGOT))
                .link('d', Shortcuts.singleStackOfItem(ItemTypes.DISPENSER))
                .row1('i', 'i', 'i').row2('i', 'd', 'i').row3('i', 'i', 'i')
                .build());
        // WAND THING (secret)
        manager.addRecipe(
                DefaultShapedRecipe.Box3By3Builder
                        .start(TBMDataManager.getWandThingStack())
                        .link('s', Shortcuts.singleStackOfItem(ItemTypes.STICK))
                        .link('r',
                                Shortcuts.singleStackOfItem(
                                        ItemTypes.ACTIVATOR_RAIL))
                .row1('s', 'r', 's').row2('s', 'r', 's').row3('s', 'r', 's')
                .build());
        Shortcuts.log().info("Loaded recipes");
    }

    private TBMRecipes() {
    }

}

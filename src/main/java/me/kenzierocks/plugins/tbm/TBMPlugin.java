/*
 * This file is part of TBMSponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) kenzierocks (Kenzie Togami) <http://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.kenzierocks.plugins.tbm;

import java.util.Objects;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;

import com.google.inject.Inject;

import me.kenzierocks.plugins.tbm.recipe.RecipeManager;
import net.minecraft.item.ItemStack;

@Plugin(id = TBMPlugin.ID, name = TBMPlugin.NAME, version = TBMPlugin.VERSION)
public class TBMPlugin {

    public static final String ID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VERSION@";
    private static TBMPlugin INSTANCE;

    public static TBMPlugin getInstance() {
        return INSTANCE;
    }

    @Inject
    private Logger logger;
    @Inject
    private RecipeManager recipeManager;

    {
        INSTANCE = this;
    }

    public Logger getLogger() {
        return this.logger;
    }

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent event) {
        this.logger.info("Loading " + NAME + " v" + VERSION);
        Sponge.getEventManager().registerListeners(this,
                new EventHandler(this));
        this.recipeManager = new RecipeManager();
        Sponge.getEventManager().registerListeners(this, this.recipeManager);
        TBMKeys.registerKeyStuff();
        // debug commands
        Sponge.getCommandDispatcher().register(this,
                CommandSpec.builder().executor(new CommandExecutor() {

                    @Override
                    public CommandResult execute(CommandSource src,
                            CommandContext args) throws CommandException {
                        if (src instanceof Player) {
                            String jsonified = ((Player) src).getItemInHand()
                                    .map(item -> ((ItemStack) item)
                                            .getTagCompound())
                                    .filter(Objects::nonNull)
                                    .map(String::valueOf).orElse("No NBT");
                            src.sendMessage(Texts.of(jsonified));
                        }
                        return CommandResult.empty();
                    }
                }).build(), "itemdata");
        // end DC
        this.logger.info("Loaded " + NAME + " v" + VERSION);
    }

    @Listener
    public void onGameInitialization(GamePostInitializationEvent event) {
        TBMRecipes.initRecipes(this.recipeManager);
    }

}

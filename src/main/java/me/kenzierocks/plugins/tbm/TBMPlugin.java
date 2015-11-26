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

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.kenzierocks.plugins.tbm.recipe.RecipeManager;

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
    private Game game;

    {
        INSTANCE = this;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Game getGame() {
        return this.game;
    }

    @Listener
    public void onGamePreInitilization(GamePreInitializationEvent event) {
        this.logger.info("Loading " + NAME + " v" + VERSION);
        this.game.getEventManager().registerListeners(this,
                new EventHandler(this));
        this.game.getEventManager().registerListeners(this,
                new RecipeManager());
        TBMKeys.registerKeyStuff(this.game);
        this.logger.info("Loaded " + NAME + " v" + VERSION);
    }

}

/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.gahvila.gahvilacore.Panilla;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class JoinQuitListener implements Listener {

    private final Panilla panilla;

    public JoinQuitListener(Panilla panilla) {
        this.panilla = panilla;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PanillaPlayer pplayer = new PanillaPlayer(event.getPlayer());
        panilla.getInventoryCleaner().clean(pplayer);
        try {
            panilla.getPlayerInjector().register(panilla, pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PanillaPlayer pplayer = new PanillaPlayer(event.getPlayer());
        try {
            panilla.getPlayerInjector().unregister(pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

}

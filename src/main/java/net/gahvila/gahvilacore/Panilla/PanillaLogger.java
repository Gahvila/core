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

import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class PanillaLogger {

    private final Panilla panilla;
    private final Logger jLogger;

    public PanillaLogger(Panilla panilla, Logger jLogger) {
        this.panilla = panilla;
        this.jLogger = jLogger;
    }

    private static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public void log(String message, boolean colorize) {
        if (colorize) {
            message = colorize(message);
        }

        if (panilla.getPConfig().consoleLogging) {
            jLogger.info(message);
        }

        if (panilla.getPConfig().chatLogging) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(PConfig.PERMISSION_LOG_CHAT)) {
                    player.sendMessage(message);
                }
            }
        }
    }

    public void info(String message, boolean colorize) {
        if (colorize) {
            message = colorize(message);
        }

        jLogger.info(message);
    }

    public void warning(String message, boolean colorize) {
        if (colorize) {
            message = colorize(message);
        }

        jLogger.warning(message);
    }

}

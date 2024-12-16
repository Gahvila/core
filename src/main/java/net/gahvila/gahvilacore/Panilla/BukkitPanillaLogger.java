package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class BukkitPanillaLogger {

    private final PanillaPlugin panilla;
    private final Logger jLogger;

    public BukkitPanillaLogger(PanillaPlugin panilla, Logger jLogger) {
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

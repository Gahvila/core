package net.gahvila.gahvilacore.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.gahvila.gahvilacore.AFK.AfkManager;
import net.gahvila.gahvilacore.Core.DebugMode;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {

    private final GahvilaCore plugin;
    private final PrefixManager prefixManager;
    private final AfkManager afkManager;
    private final PlaytimeManager playtimeManager;


    public Placeholders(GahvilaCore plugin, PrefixManager prefixManager, AfkManager afkManager, PlaytimeManager playtimeManager) {
        this.plugin = plugin;
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
        this.playtimeManager = playtimeManager;
    }


    @Override
    public String getAuthor() {
        return "gahvila";
    }

    @Override
    public String getIdentifier() {
        return "gahvilacore";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("name")) {
            return player == null ? null : player.getName(); // "name" requires the player to be valid
        }

        if (player == null) {
            return "";
        }
        Player op = player.getPlayer();
        String result = switch (params) {
            case "namecolor" -> prefixManager.generateNamecolor(op);
            case "namecolorplain" -> prefixManager.generateNamecolorPlain(op);
            case "prefix" -> prefixManager.generatePrefix(op);
            case "prefixplain" -> prefixManager.generatePrefixPlain(op);
            case "prefixandname" -> prefixManager.generatePrefixAndName(op);
            case "prefixwithoutclosing" -> prefixManager.generatePrefixWithoutClosing(op);
            case "afk" -> String.valueOf(afkManager.isPlayerAfk(op));
            case "playtime" -> PlaytimeManager.formatDuration(playtimeManager.getPlaytime(player).join());
            case "playtimeshort" -> PlaytimeManager.formatDurationShort(playtimeManager.getPlaytime(player).join());
            default -> null;
        };

        if (DebugMode.isDebugging()) {
            DebugMode.logDebugMessage("placeholder result for parameters '" + params + "' is '" + result + "'.");
        }
        return result;
    }

}
package net.gahvila.gahvilacore.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.gahvila.gahvilacore.AFK.AfkManager;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;



public class Placeholders extends PlaceholderExpansion {

    private final GahvilaCore plugin;
    private final MarriageManager marriageManager;
    private final PrefixManager prefixManager;

    public Placeholders(GahvilaCore plugin, MarriageManager marriageManager, PrefixManager prefixManager) {
        this.plugin = plugin;
        this.marriageManager = marriageManager;
        this.prefixManager = prefixManager;
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
        return switch (params) {
            case "namecolor" -> prefixManager.generateNamecolor(op);
            case "namecolorplain" -> prefixManager.generateNamecolorPlain(op);
            case "prefix" -> prefixManager.generatePrefix(op);
            case "prefixplain" -> prefixManager.generatePrefixPlain(op);
            case "prefixandname" -> prefixManager.generatePrefixAndName(op);
            case "prefixwithoutclosing" -> prefixManager.generatePrefixWithoutClosing(op);
            case "afk" -> String.valueOf(AfkManager.isPlayerAfk(op));
            default -> null;
        };
    }
}
package net.gahvila.gahvilacore.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.gahvila.gahvilacore.Essentials.AFK;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Profiles.Prefix.PrefixManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;



public class Placeholders extends PlaceholderExpansion {

    private final GahvilaCore plugin;
    private final MarriageManager marriageManager;
    private final net.gahvila.gahvilacore.Profiles.Prefix.PrefixManager prefixManager;


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
        switch (params) {
            case "playerinfo":
                if (!marriageManager.isPlayerMarried(op)){
                    return "Pelaaja <yellow>" + op.getName() + "</yellow> ei ole naimisissa.";
                }else {
                    return "Pelaaja <yellow>" + op.getName() + "</yellow> on naimisissa<br>pelaajan <yellow>" + marriageManager.getMarriageInfo(op, "currentname") + "</yellow> kanssa.";
                }
            case "namecolor":
                if (op.hasPermission("gahvilacore.rank.admin")){
                    return "<#FF4433>";
                } else if (op.hasPermission("gahvilacore.rank.pro")) {
                    return "<dark_purple>";
                } else if (op.hasPermission("gahvilacore.rank.mvp")){
                    return "<gold>";
                } else if (op.hasPermission("gahvilacore.rank.vip")){
                    return "<yellow>";
                }
                return "<aqua>";
            case "prefix":
                return prefixManager.generatePrefix(op);
            case "afk":
                if (AFK.isAfk.containsKey(player.getUniqueId())){
                    return " <gray><italic>*afk*</italic></gray>";
                }
                return "";
        }return null;
    }
}
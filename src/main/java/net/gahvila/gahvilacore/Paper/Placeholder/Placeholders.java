package net.gahvila.gahvilacore.Paper.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.gahvila.gahvilacore.Paper.Essentials.AFK;
import net.gahvila.gahvilacore.Paper.GahvilaCorePaper;
import net.gahvila.gahvilacore.Paper.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.PrefixManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;



public class Placeholders extends PlaceholderExpansion {

    private final GahvilaCorePaper plugin;
    private final MarriageManager marriageManager;


    public Placeholders(GahvilaCorePaper plugin, MarriageManager marriageManager) {
        this.plugin = plugin;
        this.marriageManager = marriageManager;
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
        LuckPerms api = LuckPermsProvider.get();
        Player op = player.getPlayer();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(op);

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
                    if (metaData.getMetaValue("prefixcolor-4") != null) {
                        return "<" + PrefixManager.getPrefix(player, 4) + ">";
                    } else {
                        return "<dark_purple>";
                    }
                } else if (op.hasPermission("gahvilacore.rank.mvp")){
                    return "<gold>";
                } else if (op.hasPermission("gahvilacore.rank.vip")){
                    return "<yellow>";
                }
                return "§b";
            case "prefix":
                String prefix = "";
                if (op.hasPermission("gahvilacore.rank.admin")) {
                    prefix = "<#FF0000><bold>Admin</bold>";
                } else if (op.hasPermission("gahvilacore.rank.pro")) {
                    if (metaData.getMetaValue("prefixcolor-4") != null) {
                        prefix = "<b><" + PrefixManager.getPrefix(player, 1) + ">P" + "<" + PrefixManager.getPrefix(player, 2) + ">r" + "<" + PrefixManager.getPrefix(player, 3) + ">o</b>";
                    } else {
                        prefix = "<b><dark_purple>Pro</dark_purple></b>";
                    }
                } else if (op.hasPermission("gahvilacore.rank.mvp")) {
                    prefix = "<gold><b>MVP</b></gold>";
                } else if (op.hasPermission("gahvilacore.rank.vip")) {
                    prefix = "<yellow><b>VIP</b></yellow>";
                }
                return prefix;
            case "afk":
                if (AFK.isAfk.containsKey(player.getUniqueId())){
                    return "<gray><italic>afk</italic></gray>";
                }
                return "";
        }return null;
    }
}
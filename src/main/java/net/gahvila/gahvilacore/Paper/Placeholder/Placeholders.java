package net.gahvila.gahvilacore.Paper.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.gahvila.gahvilacore.Paper.Essentials.AFK;
import net.gahvila.gahvilacore.Paper.GahvilaCorePaper;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.PrefixManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;



public class Placeholders extends PlaceholderExpansion {

    private final GahvilaCorePaper plugin;

    public Placeholders(GahvilaCorePaper plugin) {
        this.plugin = plugin;
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
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player.getPlayer());
        Player op = player.getPlayer();

        switch (params) {
            case "namecolor":
                if (op.hasPermission("gahvilacore.rank.admin")){
                    return "<#FF4433>";
                } else if (op.hasPermission("gahvilacore.rank.pro")) {
                    if (metaData.getMetaValue("prefixcolor-4") != null) {
                        return "§" + PrefixManager.getPrefix(player, 4);
                    } else {
                        return "§5";
                    }
                } else if (op.hasPermission("gahvilacore.rank.mvp")){
                    return "§6";
                } else if (op.hasPermission("gahvilacore.rank.vip")){
                    return "§e";
                }
                return "§b";
            case "prefix":
                String prefix = "";
                if (op.hasPermission("gahvilacore.rank.admin")) {
                    prefix = "<#FF0000><bold>Admin <reset>";
                } else if (op.hasPermission("gahvilacore.rank.pro")) {
                    if (metaData.getMetaValue("prefixcolor-4") != null) {
                        prefix = "§" + PrefixManager.getPrefix(player, 1) + ChatColor.BOLD + "P" + "§" + PrefixManager.getPrefix(player, 2) + ChatColor.BOLD + "r" + "§" + PrefixManager.getPrefix(player, 3) + ChatColor.BOLD + "o §r";
                    } else {
                        prefix = "§5" + ChatColor.BOLD + "P" + "§5" + ChatColor.BOLD + "r" + "§5" + ChatColor.BOLD + "o §r";
                    }
                } else if (op.hasPermission("gahvilacore.rank.mvp")) {
                    prefix = ChatColor.GOLD + "" + ChatColor.BOLD + "MVP §r";
                } else if (op.hasPermission("gahvilacore.rank.vip")) {
                    prefix = ChatColor.YELLOW + "" + ChatColor.BOLD + "VIP §r";
                }
                return prefix;
            case "afk":
                if (AFK.isAfk.containsKey(player.getUniqueId())){
                    return "§7§o *afk*§r";
                }
                return "";
        }return null;
    }
}
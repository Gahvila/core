package net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu;

import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PrefixManager {

    private static final Map<String, String> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("0", "black");
        COLOR_MAP.put("1", "dark_blue");
        COLOR_MAP.put("2", "dark_green");
        COLOR_MAP.put("3", "dark_aqua");
        COLOR_MAP.put("4", "dark_red");
        COLOR_MAP.put("5", "dark_purple");
        COLOR_MAP.put("6", "gold");
        COLOR_MAP.put("7", "gray");
        COLOR_MAP.put("8", "dark_gray");
        COLOR_MAP.put("9", "blue");
        COLOR_MAP.put("a", "green");
        COLOR_MAP.put("b", "aqua");
        COLOR_MAP.put("c", "red");
        COLOR_MAP.put("d", "light_purple");
        COLOR_MAP.put("e", "yellow");
        COLOR_MAP.put("f", "white");
    }
    public static String convertColor(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            String character = String.valueOf(input.charAt(i));
            if (COLOR_MAP.containsKey(character)) {
                output.append(COLOR_MAP.get(character));
            } else {
                output.append(character);
            }
        }
        return output.toString();
    }

    public static String getPrefix(OfflinePlayer player, Integer position) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player.getPlayer());
        switch (position) {
            case 1 -> {
                if (metaData.getMetaValue("prefixcolor-1") == null) {
                    return "dark_purple";
                } else {
                    if (metaData.getMetaValue("prefixcolor-" + position).length() == 1 ) {
                        setPrefix(player.getPlayer(), position, convertColor(metaData.getMetaValue("prefixcolor-" + position)));
                    }
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 2 -> {
                if (metaData.getMetaValue("prefixcolor-2") == null) {
                    return "dark_purple";
                } else {
                    if (metaData.getMetaValue("prefixcolor-" + position).length() == 1 ) {
                        setPrefix(player.getPlayer(), position, convertColor(metaData.getMetaValue("prefixcolor-" + position)));
                    }
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 3 -> {
                if (metaData.getMetaValue("prefixcolor-3") == null) {
                    return "dark_purple";
                } else {
                    if (metaData.getMetaValue("prefixcolor-" + position).length() == 1 ) {
                        setPrefix(player.getPlayer(), position, convertColor(metaData.getMetaValue("prefixcolor-" + position)));
                    }
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 4 -> {
                if (metaData.getMetaValue("prefixcolor-4") == null) {
                    return "dark_purple";
                } else {
                    if (metaData.getMetaValue("prefixcolor-" + position).length() == 1 ) {
                        setPrefix(player.getPlayer(), position, convertColor(metaData.getMetaValue("prefixcolor-" + position)));
                    }
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            default -> {
                return metaData.getMetaValue("prefixcolor-" + position);
            }
        }
    }

    public static void setPrefix(Player player, Integer position, String chatcolor) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder("prefixcolor-" + position, chatcolor).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("prefixcolor-" + position)));
        user.data().add(node);
        api.getUserManager().saveUser(user);

    }


}

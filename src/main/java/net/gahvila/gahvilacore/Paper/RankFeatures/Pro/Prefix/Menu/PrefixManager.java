package net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PrefixManager {


    public static String getPrefix(OfflinePlayer player, Integer position) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player.getPlayer());
        switch (position) {
            case 1 -> {
                if (metaData.getMetaValue("prefixcolor-1") == null) {
                    return "5";
                } else {
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 2 -> {
                if (metaData.getMetaValue("prefixcolor-2") == null) {
                    return "5";
                } else {
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 3 -> {
                if (metaData.getMetaValue("prefixcolor-3") == null) {
                    return "5";
                } else {
                    return metaData.getMetaValue("prefixcolor-" + position);
                }
            }
            case 4 -> {
                if (metaData.getMetaValue("prefixcolor-4") == null) {
                    return "5";
                } else {
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

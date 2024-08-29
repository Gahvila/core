package net.gahvila.gahvilacore.Profiles.Prefix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PrefixManager {


    public static String getPrefixType(Player player, Integer position) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("prefixtype") == null) {
            return "defaultrank";
        } else {
            return metaData.getMetaValue("prefixtype");
        }
    }

    private void setPrefixBasedOnGroup(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);

        if (metaData.getMetaValue("prefixtype") != null) {
            return;
        }
        if (isPlayerInGroup(player, "legacy")) {
            setPrefixType(player, "og");
        }
    }

    public void setPrefixType(Player player, String type) {
        setData(player, "prefixtype", type);
    }

    private void setData(Player player, String key, String value) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder(key, value).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(key)));
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }

    private boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }
}

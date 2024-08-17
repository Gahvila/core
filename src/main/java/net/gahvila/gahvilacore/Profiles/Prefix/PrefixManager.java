package net.gahvila.gahvilacore.Profiles.Prefix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static net.gahvila.gahvilacore.Profiles.Prefix.PrefixType.DEFAULT;

public class PrefixManager {


    //prefix
    public String getPrefix(Player player) {
        return LuckPermsProvider.get().getPlayerAdapter(Player.class).getMetaData(player).getPrefix();
    }

    /*
    public static void setPrefix(Player player, Integer position, String chatcolor) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder("prefixcolor-" + position, chatcolor).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("prefixcolor-" + position)));
        user.data().add(node);
        api.getUserManager().saveUser(user);

        // Load, modify & save the user in LuckPerms.
        api.getUserManager().modifyUser(player.getUniqueId(), (user1) -> {

            // Remove all other prefixes the user had before.
            user.data().clear(NodeType.PREFIX::matches);

            // Find the highest priority of their other prefixes
            // We need to do this because they might inherit a prefix from a parent group,
            // and we want the prefix we set to override that!
            Map<Integer, String> inheritedPrefixes = user.getCachedData().getMetaData(QueryOptions.nonContextual()).getPrefixes();
            int priority = inheritedPrefixes.keySet().stream().mapToInt(i -> i + 10).max().orElse(10);

            // Create a node to add to the player.
            Node node = PrefixNode.builder(prefix, priority).build();

            // Add the node to the user.
            user.data().add(node);

            // Tell the sender.
            sender.sendMessage(ChatColor.RED + user.getUsername() + " now has the prefix " + ChatColor.RESET + prefix);
        });
    }

     */

    //prefix type
    public PrefixType getPrefixType(Player player) {
        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player.getPlayer());
        if (metaData.getMetaValue("prefixtype") == null) {
            return DEFAULT;
        }
        return PrefixType.valueOf(metaData.getMetaValue("prefixtype"));
    }

    public void setPrefixType(Player player, PrefixType type) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder("prefixtype", type.toString()).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("prefixtype")));
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }
}

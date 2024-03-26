package net.gahvila.gahvilacore.Paper.Marriage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.entity.Player;

public class MarriageManager {


    public static void formMarriage(Player player, Integer position, String chatcolor) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder("prefixcolor-" + position, chatcolor).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("prefixcolor-" + position)));
        user.data().add(node);
        api.getUserManager().saveUser(user);

    }
}

package net.gahvila.gahvilacore.Paper.Marriage;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MarriageManager {

    public Boolean isPlayerMarried(Player player) {
        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String value = metaData.getMetaValue("marriedto-currentname");
        if (value == null) return false;
        if (value.equals("none#")) return false;
        return true;
    }
    public Boolean isPlayerMarriedToPlayer(Player player1, Player player2) {
        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player1);
        String value = metaData.getMetaValue("marriedto-currentname");
        return value.equals(player2.getUniqueId().toString());
    }

    public String getMarriageInfo(Player player, String info) {
        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        return switch (info) {
            case "uuid" -> metaData.getMetaValue("marriedto-uuid");
            case "currentname" -> metaData.getMetaValue("marriedto-currentname");
            default -> "";
        };
    }

    public void formMarriage(UUID player1, UUID player2) {
        //save uuid
        updateUserMarriage(player1, "marriedto-uuid", player2.toString()); //player1 marrying to player2
        updateUserMarriage(player2, "marriedto-uuid", player1.toString()); //player2 marrying to player1

        //current name
        updateUserMarriage(player1, "marriedto-currentname", Bukkit.getPlayer(player1).getName()); //player1 marrying to player2
        updateUserMarriage(player2, "marriedto-currentname", Bukkit.getPlayer(player2).getName()); //player2 marrying to player1
    }

    public void breakMarriage(UUID player1, UUID player2) {
        //save uuid
        updateUserMarriage(player1, "marriedto-uuid","none#"); //player1 marrying to player2
        updateUserMarriage(player2, "marriedto-uuid", "none#"); //player2 marrying to player1

        //current name
        updateUserMarriage(player1, "marriedto-currentname", "none#"); //player1 marrying to player2
        updateUserMarriage(player2, "marriedto-currentname", "none#"); //player2 marrying to player1
    }

    private void updateUserMarriage(UUID player, String key, String partnerName) {
        LuckPerms api = LuckPermsProvider.get();
        UserManager userManager = api.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player);

        userFuture.thenAcceptAsync(user -> {
            MetaNode node = MetaNode.builder(key, partnerName).build();
            user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(key)));
            user.data().add(node);
            api.getUserManager().saveUser(user);
        });
    }
}

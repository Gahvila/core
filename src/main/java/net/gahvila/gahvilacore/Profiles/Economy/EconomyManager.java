package net.gahvila.gahvilacore.Profiles.Economy;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyManager {

    String balanceKey = "serverbalance";

    //only use when player is online
    public double getBalance(Player player) {
        LuckPerms api = LuckPermsProvider.get();
        if (player.isOnline()){
            User user = api.getPlayerAdapter(Player.class).getUser(player);
        } else {

        }
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        return metaData.getMetaValue(balanceKey, Double::parseDouble).orElse(0.0);
    }

    public CompletableFuture<Double> getBalanceAsync(UUID who) {
        LuckPerms api = LuckPermsProvider.get();
        return api.getUserManager().loadUser(who)
                .thenApplyAsync(user -> {
                    CachedMetaData metaData = api.getPlayerAdapter(User.class).getMetaData(user);
                    return metaData.getMetaValue(balanceKey, Double::parseDouble).orElse(0.0);
                });
    }

    public void addBalance(UUID player, double amount) {
        getBalanceAsync(player).thenAcceptAsync(result -> {
            updateBalance(player, result + amount);
        });
    }

    public void removeBalance(UUID player, double amount) {
        getBalanceAsync(player).thenAcceptAsync(result -> {
            updateBalance(player, result - amount);
        });
    }

    public void setBalance(UUID player, double amount) {
        updateBalance(player, amount);
    }


    //internals
    private void updateBalance(UUID player, double balance) {
        LuckPerms api = LuckPermsProvider.get();
        UserManager userManager = api.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player);

        userFuture.thenAcceptAsync(user -> {
            MetaNode node = MetaNode.builder(balanceKey, String.valueOf(balance)).build();
            user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(balanceKey)));
            user.data().add(node);
            api.getUserManager().saveUser(user);
        });
    }
}

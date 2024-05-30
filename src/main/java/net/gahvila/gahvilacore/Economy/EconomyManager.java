package net.gahvila.gahvilacore.Economy;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyManager {

    String balanceKey = "serverbalance";

    public Double getBalance(UUID player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = (User) api.getUserManager().loadUser(player);
        CachedMetaData metaData = api.getPlayerAdapter(User.class).getMetaData(user);
        return Double.parseDouble(metaData.getMetaValue(balanceKey));
    }

    public void addBalance(UUID player, double amount) {
        String balance = String.valueOf(getBalance(player) + amount);
        updateBalance(player, balance);
    }

    public void removeBalance(UUID player, double amount) {
        String balance = String.valueOf(getBalance(player) - amount);
        updateBalance(player, balance);
    }

    public void setBalance(UUID player, double amount) {
        String balance = String.valueOf(amount);
        updateBalance(player, balance);
    }

    private void updateBalance(UUID player, String balance) {
        LuckPerms api = LuckPermsProvider.get();
        UserManager userManager = api.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player);

        userFuture.thenAcceptAsync(user -> {
            MetaNode node = MetaNode.builder(balanceKey, balance).build();
            user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(balanceKey)));
            user.data().add(node);
            api.getUserManager().saveUser(user);
        });
    }
}

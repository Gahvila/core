package net.gahvila.gahvilacore.Profiles.Prefix;

import net.gahvila.gahvilacore.Profiles.Prefix.PrefixType.Gradient;
import net.gahvila.gahvilacore.Profiles.Prefix.PrefixType.Single;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.entity.Player;

import java.util.Map;

public class PrefixManager {

    //prefix generator
    public String generatePrefix(Player player) {
        String prefix = getPrefix(player).getDisplayName();

        PrefixTypes type = getPrefixType(player);
        return switch(type) {
            case GRADIENT -> "<gradient:" + getGradient(player) + "><b>" + prefix + "</b>" + " " + player.getName() + "</gradient>";
            case SINGLE -> "<" + getSingle(player) + "><b>" + prefix + "</b>" + " " + player.getName() + "</" + getSingle(player) + ">";
        };
    }

    //prefix
    public Prefix getPrefix(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("prefix") == null) {
            return getPrefixBasedOnGroup(player);
        } else {
            return Prefix.valueOf(metaData.getMetaValue("prefix"));
        }
    }

    public void setPrefix(Player player, Prefix type) {
        setData(player, "prefix", type.toString());
    }

    //prefix type
    public PrefixTypes getPrefixType(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("prefix") == null) {
            return PrefixTypes.SINGLE;
        } else {
            return PrefixTypes.valueOf(metaData.getMetaValue("prefixtype"));
        }
    }

    public void setPrefixType(Player player, PrefixTypes type) {
        setData(player, "prefixtype", type.toString());
    }

    //gradient
    public String getGradient(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("gradient") == null) {
            return Gradient.KAHVI.getGradient();
        } else {
            return Gradient.valueOf(metaData.getMetaValue("gradient")).getGradient();
        }
    }

    public void setGradient(Player player, Gradient gradient) {
        setData(player, "gradient", gradient.toString());
    }

    //single
    public String getSingle(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("single") == null) {
            return Single.KELTAINEN.getColor();
        } else {
            return Single.valueOf(metaData.getMetaValue("single")).getColor();
        }
    }

    public void setSingle(Player player, Single type) {
        setData(player, "single", type.toString());
    }

    //internal stuff to the class

    private static final Map<String, Prefix> GROUP_PREFIX_MAP = Map.of(
            "admin", Prefix.ADMIN,
            "legacy", Prefix.OG,
            "espresso", Prefix.ESPRESSO,
            "cortado", Prefix.CORTADO,
            "cappuccino", Prefix.CAPPUCCINO,
            "latte", Prefix.LATTE,
            "mocha", Prefix.MOCHA
    );

    private Prefix getPrefixBasedOnGroup(Player player) {
        // check which group player is in
        return GROUP_PREFIX_MAP.entrySet().stream()
                .filter(entry -> isPlayerInGroup(player, entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Prefix.DEFAULT);
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

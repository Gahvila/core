package net.gahvila.gahvilacore.Profiles.Prefix.Backend;

import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Gradient;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixTypes;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.entity.Player;

public class PrefixManager {

    //generators
    public String generatePrefix(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";
        String prefix = getPrefix(player).getDisplayName();

        PrefixTypes type = getPrefixType(player);
        if (getPrefix(player) == Prefix.DEFAULT) {
            return switch (type) {
                case GRADIENT -> "<gradient:" + getGradient(player) + "></gradient>";
                case SINGLE -> "<" + getSingle(player) + "></" + getSingle(player) + ">";
            };
        } else {
            return switch (type) {
                case GRADIENT -> "<gradient:" + getGradient(player) + "><b>" + prefix + "</b></gradient>";
                case SINGLE -> "<" + getSingle(player) + "><b>" + prefix + "</b>" + "</" + getSingle(player) + ">";
            };
        }
    }

    public String generatePrefixWithoutClosing(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";
        String prefix = getPrefix(player).getDisplayName();

        PrefixTypes type = getPrefixType(player);

        if (getPrefix(player) == Prefix.DEFAULT) {
            return switch (type) {
                case GRADIENT -> "<gradient:" + getGradient(player) + ">";
                case SINGLE -> "<" + getSingle(player) + ">";
            };
        }else {
            return switch (type) {
                case GRADIENT -> "<gradient:" + getGradient(player) + "><b>" + prefix + "</b> ";
                case SINGLE -> "<" + getSingle(player) + "><b>" + prefix + "</b> ";
            };
        }
    }

    public String generatePrefixAndName(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return player.getName();
        String prefix = getPrefix(player).getDisplayName();

        PrefixTypes type = getPrefixType(player);
        if (getPrefix(player) == Prefix.DEFAULT) {
            return switch (type) {
                case GRADIENT ->
                        "<gradient:" + getGradient(player) + ">" + player.getName() + "</gradient>";
                case SINGLE ->
                        "<" + getSingle(player) + ">" + player.getName() + "</" + getSingle(player) + ">";
            };
        } else {
            return switch (type) {
                case GRADIENT ->
                        "<gradient:" + getGradient(player) + "><b>" + prefix + "</b>" + " " + player.getName() + "</gradient>";
                case SINGLE ->
                        "<" + getSingle(player) + "><b>" + prefix + "</b>" + " " + player.getName() + "</" + getSingle(player) + ">";
            };
        }
    }

    public String generatePrefixPlain(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";
        String prefix = getPrefix(player).getDisplayName();

        return prefix;
    }

    public String generateNamecolor(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";

        PrefixTypes type = getPrefixType(player);
        return switch(type) {
            case GRADIENT -> "<gradient:" + getGradient(player) + ">";
            case SINGLE -> "<" + getSingle(player) + ">";
        };
    }

    public String generateNamecolorPlain(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";

        PrefixTypes type = getPrefixType(player);
        return switch(type) {
            case GRADIENT -> getGradient(player);
            case SINGLE -> getSingle(player);
        };
    }

    //prefix
    public Prefix getPrefix(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String prefixValue = metaData.getMetaValue("prefix");

        if (prefixValue == null) return getPrefixBasedOnGroup(player);

        try {
            return Prefix.valueOf(metaData.getMetaValue("prefix"));
        } catch (IllegalArgumentException e) {
            return getPrefixBasedOnGroup(player);
        }
    }

    public void setPrefix(Player player, Prefix type) {
        setData(player, "prefix", type.toString());
    }

    //prefix type
    public PrefixTypes getPrefixType(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        if (metaData.getMetaValue("prefixtype") == null) {
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
        String gradientValue = metaData.getMetaValue("gradient");

        if (gradientValue == null) return Gradient.KAHVI.getGradient();

        try {
            return Gradient.valueOf(gradientValue.toUpperCase()).getGradient();
        } catch (IllegalArgumentException e) {
            return Gradient.KAHVI.getGradient();
        }
    }

    public void setGradient(Player player, Gradient gradient) {
        setData(player, "gradient", gradient.toString());
    }

    //single
    public String getSingle(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String singleValue = metaData.getMetaValue("single");

        if (singleValue == null) return Single.TURKOOSI.getColor();

        try {
            return Single.valueOf(singleValue).getColor();
        } catch (IllegalArgumentException e) {
            return Single.TURKOOSI.getColor();
        }
    }

    public void setSingle(Player player, Single type) {
        setData(player, "single", type.toString());
    }

    //internal stuff to the class
    private Prefix getPrefixBasedOnGroup(Player player) {
        // check which group player is in
        if (isPlayerInGroup(player, "admin")) {
            return Prefix.ADMIN;
        } else if (isPlayerInGroup(player, "legacy")) {
            return Prefix.OG;
        } else if (isPlayerInGroup(player, "espresso")) {
            return Prefix.ESPRESSO;
        } else if (isPlayerInGroup(player, "cortado")) {
            return Prefix.CORTADO;
        } else if (isPlayerInGroup(player, "latte")) {
            return Prefix.LATTE;
        } else if (isPlayerInGroup(player, "mocha")) {
            return Prefix.MOCHA;
        } else {
            return Prefix.DEFAULT;
        }
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

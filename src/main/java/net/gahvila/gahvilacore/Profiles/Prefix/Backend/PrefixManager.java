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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrefixManager {

    private final Map<UUID, PlayerCache> cache = new ConcurrentHashMap<>();

    private static class PlayerCache {
        Prefix prefix;
        PrefixTypes prefixType;
        String gradient;
        String single;
    }

    public void invalidateCache(Player player) {
        cache.remove(player.getUniqueId());
    }

    private PlayerCache getCache(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), k -> new PlayerCache());
    }

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
        } else {
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
        return getPrefix(player).getDisplayName();
    }

    public String generateNamecolor(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";

        PrefixTypes type = getPrefixType(player);
        return switch (type) {
            case GRADIENT -> "<gradient:" + getGradient(player) + ">";
            case SINGLE -> "<" + getSingle(player) + ">";
        };
    }

    public String generateNamecolorPlain(Player player) {
        if (!isPlayerInGroup(player, "legacy")) return "";

        PrefixTypes type = getPrefixType(player);
        return switch (type) {
            case GRADIENT -> getGradient(player);
            case SINGLE -> getSingle(player);
        };
    }

    //prefix
    public Prefix getPrefix(Player player) {
        PlayerCache data = getCache(player);
        if (data.prefix != null) return data.prefix;

        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String prefixValue = metaData.getMetaValue("prefix");

        Prefix result;
        if (prefixValue == null) {
            result = getPrefixBasedOnGroup(player);
        } else {
            try {
                result = Prefix.valueOf(prefixValue);
            } catch (IllegalArgumentException e) {
                result = getPrefixBasedOnGroup(player);
            }
        }

        data.prefix = result;
        return result;
    }

    public void setPrefix(Player player, Prefix type) {
        setData(player, "prefix", type.toString());
    }

    //prefix type
    public PrefixTypes getPrefixType(Player player) {
        PlayerCache data = getCache(player);
        if (data.prefixType != null) return data.prefixType;

        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);

        PrefixTypes result;
        if (metaData.getMetaValue("prefixtype") == null) {
            result = PrefixTypes.SINGLE;
        } else {
            try {
                result = PrefixTypes.valueOf(metaData.getMetaValue("prefixtype"));
            } catch (IllegalArgumentException e) {
                result = PrefixTypes.SINGLE;
            }
        }

        data.prefixType = result;
        return result;
    }

    public void setPrefixType(Player player, PrefixTypes type) {
        setData(player, "prefixtype", type.toString());
    }

    //gradient
    public String getGradient(Player player) {
        PlayerCache data = getCache(player);
        if (data.gradient != null) return data.gradient;

        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String gradientValue = metaData.getMetaValue("gradient");

        String result;
        if (gradientValue == null) {
            result = Gradient.KAHVI.getGradient();
        } else {
            try {
                result = Gradient.valueOf(gradientValue.toUpperCase()).getGradient();
            } catch (IllegalArgumentException e) {
                result = Gradient.KAHVI.getGradient();
            }
        }

        data.gradient = result;
        return result;
    }

    public void setGradient(Player player, Gradient gradient) {
        setData(player, "gradient", gradient.toString());
    }

    //single
    public String getSingle(Player player) {
        PlayerCache data = getCache(player);
        if (data.single != null) return data.single;

        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String singleValue = metaData.getMetaValue("single");

        String result;
        if (singleValue == null) {
            result = Single.TURKOOSI.getColor();
        } else {
            try {
                result = Single.valueOf(singleValue).getColor();
            } catch (IllegalArgumentException e) {
                result = Single.TURKOOSI.getColor();
            }
        }

        data.single = result;
        return result;
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
        // Invalidate cache immediately so next get() fetches new data
        invalidateCache(player);

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
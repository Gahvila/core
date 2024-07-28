package net.gahvila.gahvilacore.Profiles.Prefix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PrefixManager {


    //prefix
    public String getPrefix(Player player) {
        return LuckPermsProvider.get().getPlayerAdapter(Player.class).getMetaData(player).getPrefix();
    }

    public void setPrefix(Player player, String prefix) {

    }


    //prefix type
    public String getPrefixType(Player player) {
        LuckPerms api = LuckPermsProvider.get();
        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player.getPlayer());
        if (metaData.getMetaValue("prefixtype") == null) {
            return "default";
        }
        return metaData.getMetaValue("prefixtype");
    }
}

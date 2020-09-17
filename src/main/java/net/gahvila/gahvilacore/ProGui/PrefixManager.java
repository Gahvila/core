package net.gahvila.gahvilacore.ProGui;

import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PrefixManager {

    public static void enablePrefix(Player player, boolean enabled) {

        File prefixdata = new File(GahvilaCore.instance.getDataFolder(), "prefixdata.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(prefixdata);

        try {

            String uuid = player.getUniqueId().toString();
            f.set(uuid + ".chatformatenabled", enabled);
            f.save(prefixdata);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isPrefixEnabled(Player player) {

        File prefixdata = new File(GahvilaCore.instance.getDataFolder(), "prefixdata.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(prefixdata);

        String uuid = player.getUniqueId().toString();

        if (f.contains(uuid)) {
            if (!f.contains(uuid + ".chatformatenabled")) return false;

            return f.getBoolean(uuid + ".chatformatenabled");
        }
        return false;
    }

    public static void setPrefix(Player player, Integer position, String chatcolor) {

        File prefixdata = new File(GahvilaCore.instance.getDataFolder(), "prefixdata.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(prefixdata);

        try {

            String uuid = player.getUniqueId().toString();
            f.set(uuid + "." + position, chatcolor);
            f.save(prefixdata);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public static String getPrefix(Player player, Integer position) {

        File prefixdata = new File(GahvilaCore.instance.getDataFolder(), "prefixdata.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(prefixdata);

        String uuid = player.getUniqueId().toString();

        if (f.contains(uuid)) {
            if (!f.contains(uuid + "." + position)) {
                if (position == 4) {
                    return "7";
                }
                return "5";
            }

            String s = f.getString(uuid + "." + position);
            return s;
        }
        if (position == 4) {
            return "7";
        }
        return "5";

    }
}

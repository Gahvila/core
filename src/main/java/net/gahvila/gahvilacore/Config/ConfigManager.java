package net.gahvila.gahvilacore.Config;

import de.leonhard.storage.Yaml;
import org.bukkit.entity.Player;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class ConfigManager {

    public static String getDownloadUsername() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("download-server-username", "admin");

    }

    public static String getDownloadPassword() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("download-server-password", "1234");
    }

    public static String getDownloadUrl() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("download-server-url", "");
    }
}

package net.gahvila.gahvilacore.Config;

import de.leonhard.storage.Yaml;

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

    public static String getServerName() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("server-name", "unknown").toLowerCase();
    }
}

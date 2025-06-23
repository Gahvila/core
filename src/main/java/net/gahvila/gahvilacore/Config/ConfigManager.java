package net.gahvila.gahvilacore.Config;

import de.leonhard.storage.Yaml;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class ConfigManager {

    // DOWNLOAD SERVER
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


    // DATABASE
    public static String getDatabaseJdbcUrl() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("database-jdbc-url", "");
    }

    public static String getDatabaseUsername() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("database-username", "");
    }

    public static String getDatabasePassword() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("database-password", "");
    }

    public static Integer getDatabasePoolSize() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("database-pool-size", 10);
    }


    // MISC
    public static String getServerName() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("server-name", "unknown").toLowerCase();
    }

    public static Boolean getPanilla() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("panilla", false);
    }
}

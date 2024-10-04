package net.gahvila.gahvilacore.Teleport;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class TeleportManager {

    public static HashMap<String, Location> teleportCache = new HashMap<>();

    public void saveTeleport(String type, Location location) {
        Json tpData = new Json("teleportdata.json", instance.getDataFolder() + "/data/");
        tpData.getFileData().insert(type + ".world", location.getWorld().getName());
        tpData.getFileData().insert(type + ".x", location.getX());
        tpData.getFileData().insert(type + ".y", location.getY());
        tpData.getFileData().insert(type + ".z", location.getZ());
        tpData.getFileData().insert(type + ".yaw", location.getYaw());
        tpData.set(type + ".pitch", location.getPitch());
        teleportCache.put(type, location);
    }

    public Location getTeleport(String type) {
        if (!teleportCache.containsKey(type)) {
            putTeleportsIntoCache();
            Bukkit.getLogger().info("Location '" + type + "' was not found in cache, added.");
        }

        if (teleportCache.get(type) == null) {
            Bukkit.getLogger().warning("Location '" + type + "' still not found in cache, reading from disk.");
            return getTeleportFromStorage(type);
        }
        return teleportCache.get(type);
    }

    public Location getTeleportFromStorage(String type) {
        Json tpData = new Json("teleportdata.json", instance.getDataFolder() + "/data/");
        if (tpData.getFileData().containsKey(type)) {
            World world = Bukkit.getWorld(tpData.getString(type + ".world"));
            double x = tpData.getDouble(type + ".x");
            double y = tpData.getDouble(type + ".y");
            double z = tpData.getDouble(type + ".z");
            float yaw = (float) tpData.getDouble(type + ".yaw");
            float pitch = (float) tpData.getDouble(type + ".pitch");
            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }

    public ArrayList<String> getTeleportsFromStorage() {
        Json tpData = new Json("teleportdata.json", instance.getDataFolder() + "/data/");
        return new ArrayList<>(tpData.getFileData().singleLayerKeySet());
    }
    public void putTeleportsIntoCache(){
        ArrayList<String> teleportTypes = getTeleportsFromStorage();
        for (String type : teleportTypes) {
            Location location = getTeleportFromStorage(type);
            teleportCache.put(type, location);
        }
    }
}

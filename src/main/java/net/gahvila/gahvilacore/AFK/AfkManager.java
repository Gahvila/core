package net.gahvila.gahvilacore.AFK;

import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeCache;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class AfkManager {


    private static final HashMap<UUID, Boolean> isAfk = new HashMap<>();
    private static final HashMap<UUID, Location> lastLoc = new HashMap<>();
    private static final HashMap<UUID, Long> lastAction = new HashMap<>();

    private static final long AFK_TIME_LIMIT = 60000; // AFK timeout (1 min) in milliseconds

    public static void setPlayerAFK(Player player, boolean afk) {
        UUID uuid = player.getUniqueId();
        PlaytimeCache playtimeCache = PlaytimeManager.playtimeCache.get(uuid);

        if (playtimeCache != null) {
            playtimeCache.setAfk(afk);
        }

        lastAction.put(uuid, System.currentTimeMillis());

        if (afk) {
            isAfk.put(uuid, true);
            player.sendMessage("Olet nyt afk.");
        } else {
            isAfk.remove(uuid);
            player.sendMessage("Et ole enää afk.");
        }
    }

    public static boolean isPlayerAfk(UUID uuid) {
        return isAfk.containsKey(uuid);
    }

    public static boolean isPlayerAfk(Player player) {
        return isAfk.containsKey(player.getUniqueId());
    }

    public static void startAfkScheduler() {
        Bukkit.getScheduler().runTaskTimer(instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                if (isAfk.containsKey(uuid)) continue;

                Location currentLocation = player.getLocation();
                long currentTime = System.currentTimeMillis();
                long lastMovedTime = lastAction.getOrDefault(uuid, currentTime);

                if (lastLoc.get(uuid) != null && lastLoc.get(uuid).equals(currentLocation)) {
                    if (currentTime - lastMovedTime >= AFK_TIME_LIMIT) {
                        setPlayerAFK(player, true);
                    }
                } else {
                    lastLoc.put(uuid, currentLocation);
                    lastAction.put(uuid, currentTime);
                }
            }
        }, 0, 20);
    }

    public static void cleanupPlayerData(UUID uuid) {
        isAfk.remove(uuid);
        lastLoc.remove(uuid);
        lastAction.remove(uuid);
    }
}
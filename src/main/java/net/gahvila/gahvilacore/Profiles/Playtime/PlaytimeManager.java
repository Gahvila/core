package net.gahvila.gahvilacore.Profiles.Playtime;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.Essentials.AFK;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class PlaytimeManager {
    public static final String PLAYTIME_KEY = "playtime-";
    public static final HashMap<UUID, PlaytimeCache> playtimeCache = new HashMap<>();
    private static final String SERVER_NAME = ConfigManager.getServerName();

    public void loadPlayerIntoCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        long joinTime = System.currentTimeMillis();

        getPlaytime(player).thenAccept(playtime -> {
            PlaytimeCache playtimeCache = new PlaytimeCache(playtime, joinTime);
            PlaytimeManager.playtimeCache.put(playerUUID, playtimeCache);
        }).thenRun(() -> {
            addStatsPlaytime(player);
        });
    }

    public void saveCachedPlaytimeToLuckPerms(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playtimeCache.containsKey(playerUUID)) {
            PlaytimeCache playtimeCache = PlaytimeManager.playtimeCache.get(playerUUID);

            // Don't update playtime if player is AFK
            if (AFK.isAfk.getOrDefault(playerUUID, false)) {
                return;
            }

            Long elapsedTime = playtimeCache.getElapsedTime();
            playtimeCache.addPlaytime(elapsedTime);
            setPlaytime(player, playtimeCache.getPlaytime());

            playtimeCache.setLastJoinTime(System.currentTimeMillis());
        }
    }

    public void startScheduledSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerUUID : playtimeCache.keySet()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null && player.isOnline()) {
                        saveCachedPlaytimeToLuckPerms(player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 6000L);
    }

    public void onPlayerLeave(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (playtimeCache.containsKey(playerUUID)) {
            saveCachedPlaytimeToLuckPerms(player);
            playtimeCache.remove(playerUUID);
        }
    }

    public CompletableFuture<Long> getPlaytime(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();

        return getUser(playerUUID).thenApplyAsync(user -> {
            if (user == null) {
                return 0L;
            }

            if (playtimeCache.containsKey(playerUUID)) {
                PlaytimeCache playtimeCache = PlaytimeManager.playtimeCache.get(playerUUID);
                return playtimeCache.getPlaytime() + playtimeCache.getElapsedTime();
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String playtimeValue = metaData.getMetaValue(PLAYTIME_KEY + SERVER_NAME);

            if (playtimeValue == null) {
                return 0L;
            }

            try {
                return Long.parseLong(playtimeValue);
            } catch (Exception e) {
                return 0L;
            }
        });
    }

    public CompletableFuture<Void> setPlaytime(OfflinePlayer player, Long playtime) {
        LuckPerms api = LuckPermsProvider.get();
        UUID playerUUID = player.getUniqueId();

        return getUser(playerUUID).thenAcceptAsync(user -> {
            if (user == null) return;

            MetaNode playtimeNode = MetaNode.builder(PLAYTIME_KEY + SERVER_NAME, playtime.toString()).build();
            user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(PLAYTIME_KEY + SERVER_NAME)));
            user.data().add(playtimeNode);

            api.getUserManager().saveUser(user);
        });
    }

    public CompletableFuture<Void> addPlaytime(OfflinePlayer player, Long playtime) {
        if (player.isOnline()){
            playtimeCache.get(player.getUniqueId()).addPlaytime(playtime);
            return CompletableFuture.completedFuture(null);
        }
        return getPlaytime(player).thenCompose(currentPlaytime -> setPlaytime(player, playtime + currentPlaytime));
    }

    public CompletableFuture<Void> reducePlaytime(OfflinePlayer player, Long playtime) {
        if (player.isOnline()){
            playtimeCache.get(player.getUniqueId()).subtractPlaytime(playtime);
            return CompletableFuture.completedFuture(null);
        }
        return getPlaytime(player).thenCompose(currentPlaytime -> {
            long newPlaytime = currentPlaytime - playtime;
            if (newPlaytime < 0) {
                newPlaytime = 0L;
            }
            return setPlaytime(player, newPlaytime);
        });
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        return luckPerms.getUserManager().loadUser(uuid);
    }

    public CompletableFuture<Void> resetPlaytime(OfflinePlayer player) {
        return setPlaytime(player, 0L);
    }

    public static String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append(" tunti").append(hours > 1 ? "a" : "");
        }

        if (minutes > 0) {
            if (!result.isEmpty()) {
                result.append(", ");
            }
            result.append(minutes).append(" minuutti").append(minutes > 1 ? "a" : "");
        }

        if (remainingSeconds > 0) {
            if (!result.isEmpty()) {
                result.append(", ");
            }
            result.append(remainingSeconds).append(" sekunti").append(remainingSeconds > 1 ? "a" : "");
        }

        if (!result.isEmpty()) {
            int lastComma = result.lastIndexOf(",");
            if (lastComma != -1) {
                result.replace(lastComma, lastComma + 1, " ja");
            }
        } else {
            return "0 sekuntia";
        }

        return result.toString();
    }

    public void setHasConverted(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "playtimeConverted", true);
    }

    public boolean getHasConverted(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        return playerData.getFileData().containsKey(uuid + "." + "playtimeConverted");
    }

    public void addStatsPlaytime(Player player) {
        if (!getHasConverted(player)){
            long amount = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
            addPlaytime(player, amount);
            setHasConverted(player);
        }
    }
}
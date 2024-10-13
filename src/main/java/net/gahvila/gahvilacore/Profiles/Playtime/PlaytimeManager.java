package net.gahvila.gahvilacore.Profiles.Playtime;

import net.gahvila.gahvilacore.Config.ConfigManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class PlaytimeManager {
    public static final String PLAYTIME_KEY = "playtime-";
    private static final HashMap<UUID, CachedPlaytime> playtimeCache = new HashMap<>();
    private static final String SERVER_NAME = ConfigManager.getServerName();

    public void loadPlayerIntoCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        long joinTime = System.currentTimeMillis();

        getPlaytime(player).thenAccept(playtime -> {
            CachedPlaytime cachedPlaytime = new CachedPlaytime(playtime, joinTime);
            playtimeCache.put(playerUUID, cachedPlaytime);
        });
    }

    public void saveCachedPlaytimeToLuckPerms(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (playtimeCache.containsKey(playerUUID)) {
            CachedPlaytime cachedPlaytime = playtimeCache.get(playerUUID);

            Long elapsedTime = cachedPlaytime.getElapsedTime();
            cachedPlaytime.addPlaytime(elapsedTime);
            setPlaytime(player, cachedPlaytime.getPlaytime());

            cachedPlaytime.setLastJoinTime(System.currentTimeMillis());
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

    public CompletableFuture<Duration> getPlaytimeDuration(OfflinePlayer player) {
        return getPlaytime(player).thenApplyAsync(Duration::ofSeconds);
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

    public CompletableFuture<User> getUser(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        return luckPerms.getUserManager().loadUser(uuid);
    }

    public CompletableFuture<Void> resetPlaytime(OfflinePlayer player) {
        return setPlaytime(player, 0L);
    }
}
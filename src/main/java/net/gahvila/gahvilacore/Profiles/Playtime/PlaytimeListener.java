package net.gahvila.gahvilacore.Profiles.Playtime;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeListener implements Listener {

    private final PlaytimeManager playtimeManager;

    public PlaytimeListener(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playtimeManager.loadPlayerIntoCache(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playtimeManager.onPlayerLeave(player);
    }
}

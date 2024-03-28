package net.gahvila.gahvilacore.Paper;

import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerSleeper implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        ServerTickManager serverTickManager = Bukkit.getServerTickManager();
        if (serverTickManager.getTickRate() <= 20) {
            serverTickManager.setFrozen(false);
            serverTickManager.setTickRate(20);
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        ServerTickManager serverTickManager = Bukkit.getServerTickManager();
        if (Bukkit.getOnlinePlayers().isEmpty()){
            serverTickManager.setTickRate(1);
            serverTickManager.setFrozen(true);
        }
    }
}

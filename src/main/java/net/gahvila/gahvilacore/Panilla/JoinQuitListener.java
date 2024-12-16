package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class JoinQuitListener implements Listener {

    private final PanillaPlugin panilla;

    public JoinQuitListener(PanillaPlugin panilla) {
        this.panilla = panilla;
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BukkitPanillaPlayer pplayer = new BukkitPanillaPlayer(event.getPlayer());
        panilla.getInventoryCleaner().clean(pplayer);
        try {
            panilla.getPlayerInjector().register(panilla, pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BukkitPanillaPlayer pplayer = new BukkitPanillaPlayer(event.getPlayer());
        try {
            panilla.getPlayerInjector().unregister(pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

}

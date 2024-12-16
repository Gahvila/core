package net.gahvila.gahvilacore.Panilla;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class JoinQuitListener implements Listener {

    private final Panilla panilla;

    public JoinQuitListener(Panilla panilla) {
        this.panilla = panilla;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PanillaPlayer pplayer = new PanillaPlayer(event.getPlayer());
        panilla.getInventoryCleaner().clean(pplayer);
        try {
            panilla.getPlayerInjector().register(panilla, pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PanillaPlayer pplayer = new PanillaPlayer(event.getPlayer());
        try {
            panilla.getPlayerInjector().unregister(pplayer);
        } catch (IOException e) {
            // Ignore
        }
    }

}

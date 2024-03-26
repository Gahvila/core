package net.gahvila.gahvilacore.Paper.RankFeatures.VIP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class FullBypass implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) {
            if (e.getPlayer().hasPermission("gahvilacore.playerlimit.bypass")) {
                e.allow();
                e.getPlayer().sendMessage("§eOhitit palvelimen maksimi pelaajamäärän.");
            }
        }
    }
}
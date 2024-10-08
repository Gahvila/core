package net.gahvila.gahvilacore.RankFeatures;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class FullBypass implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) {
            if (e.getPlayer().hasPermission("gahvilacore.playerlimit.bypass")) {
                e.allow();
                e.getPlayer().sendMessage(toMM("§eOhitit palvelimen maksimi pelaajamäärän."));
            }
        }
    }
}
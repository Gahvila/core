package net.gahvila.gahvilacore.AFK;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class AfkEvents implements Listener {

    private final AfkManager afkManager;

    public AfkEvents(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            afkManager.setPlayerAFK(player, false);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            event.setCancelled(true);
            player.sendMessage("Viestiä ei lähetetty, koska olet AFK-tilassa.");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/afk")) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            afkManager.setPlayerAFK(player, false);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            afkManager.setPlayerAFK(player, false);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            afkManager.setPlayerAFK(player, false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (afkManager.isPlayerAfk(uuid)) {
            afkManager.setPlayerAFK(player, false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        afkManager.cleanupPlayerData(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        afkManager.cleanupPlayerData(uuid);
    }
}

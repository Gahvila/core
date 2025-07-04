package net.gahvila.gahvilacore.Teleport.Spawn;

import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpawnTeleport implements Listener {

    private final TeleportManager teleportManager;

    public SpawnTeleport(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean firstJoin = !player.hasPlayedBefore();

        if ((firstJoin && ConfigManager.getIfSpawnOnFirstJoin()) ||
                (!firstJoin && ConfigManager.getIfSpawnOnJoin())) {
            Location spawnLoc = teleportManager.getTeleport(GahvilaCore.instance, "spawn");
            if (spawnLoc != null) {
                player.teleportAsync(spawnLoc);
            } else {
                player.sendMessage("Spawn-sijaintia ei ole asetettu.");
            }
        }
    }
}

package net.gahvila.gahvilacore.Teleport.Spawn;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.Location;
import org.bukkit.Sound;

import static java.lang.Float.MAX_VALUE;

public class SpawnCommand {
    private final TeleportManager teleportManager;

    public SpawnCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    public void registerCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((p, args) -> {
                    Location loc = teleportManager.getTeleport(GahvilaCore.instance, "spawn");
                    if (loc == null) {
                        p.sendMessage("Spawn-sijaintia ei ole asetettu.");
                        return;
                    }
                    p.teleportAsync(loc);
                    p.sendMessage("Teleporttasit spawnille.");
                    p.playSound(loc, Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
                })
                .register();
    }
}

package net.gahvila.gahvilacore.Essentials;

import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.UUID;

public class AFK implements Listener {

    public static HashMap<UUID, Boolean> isAfk = new HashMap();
    public static HashMap<UUID, Location> lastLoc = new HashMap<>();
    public static HashMap<UUID, Long> lastAction = new HashMap<>();

    private final GahvilaCore plugin;

    public AFK(GahvilaCore plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        new CommandAPICommand("afk")
                .executesPlayer((p, args) -> {
                    UUID uuid = p.getUniqueId();
                    if (!isAfk.containsKey(uuid)){
                        isAfk.put(uuid, true);
                        p.sendMessage("Olet nyt afk.");
                    }else{
                        p.sendMessage("Et ole enää afk.");
                        isAfk.remove(uuid);
                    }
                })
                .register();
    }

    int taskID;
    public void afkScheduler() {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()){
                UUID uuid = player.getUniqueId();
                if (isAfk.containsKey(uuid)) return;
                Location currentLocation = player.getLocation();
                // If lastLoc is null or the currentLocation is different from the last one
                if (lastLoc.get(uuid) == null || !lastLoc.get(uuid).equals(currentLocation)) {
                    long unixTime = System.currentTimeMillis();
                    long lastMovedTime = lastAction.containsKey(uuid) ? lastAction.get(uuid) : 0;
                    // If it has been 2.5 minutes since the player has moved
                    if (unixTime - lastMovedTime >= 150000){
                        isAfk.put(uuid, true);
                        player.sendMessage("Olet nyt afk.");
                    }
                }
            }
        }, 0, 20).getTaskId();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if (!(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ())) return;
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(AsyncChatEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(p.getUniqueId(), System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            e.setCancelled(true);
            p.sendMessage("Suojataksesi sinua laittamasta viestejä vahingossa chattiin, viestin lähetys estettiin koska olet AFK tilassa.");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if (e.getMessage().toLowerCase().startsWith("/afk")){
            return;
        }
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerInteractEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(BlockBreakEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(BlockPlaceEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerJoinEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        isAfk.remove(uuid);
        lastLoc.remove(uuid);
        lastAction.remove(uuid);
    }
}

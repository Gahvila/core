package net.gahvila.gahvilacore.Paper.Essentials;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import it.unimi.dsi.fastutil.Hash;
import net.gahvila.gahvilacore.Paper.GahvilaCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFK implements CommandExecutor, Listener {

    public static HashMap<UUID, Boolean> isAfk = new HashMap();
    public static HashMap<UUID, Location> lastLoc = new HashMap<>();
    public static HashMap<UUID, Long> lastMoved = new HashMap<>();

    private final GahvilaCorePaper plugin;

    public AFK(GahvilaCorePaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = ((Player) sender).getPlayer();
            UUID uuid = p.getUniqueId();
            if (!isAfk.containsKey(uuid)){
                isAfk.put(uuid, true);
                p.sendMessage("Olet nyt afk.");
            }else{
                p.sendMessage("Et ole enää afk.");
                isAfk.remove(uuid);
            }
            return true;
        }
        return true;
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
                    long lastMovedTime = lastMoved.containsKey(uuid) ? lastMoved.get(uuid) : 0;

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
        lastMoved.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(AsyncChatEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            e.setCancelled(true);
            p.sendMessage("Suojataksesi sinua laittamasta viestejä vahingossa chattiin, viestin lähetys estettiin koska olet AFK tilassa.");
        }
    }

    @EventHandler
    public void onMove(PlayerInteractEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastMoved.put(uuid, System.currentTimeMillis());
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
        lastMoved.put(uuid, System.currentTimeMillis());
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
        lastMoved.put(uuid, System.currentTimeMillis());
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
        lastMoved.put(uuid, System.currentTimeMillis());
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
        lastMoved.remove(uuid);
    }
}

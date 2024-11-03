package net.gahvila.gahvilacore.Essentials;

import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeCache;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
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

    public static HashMap<UUID, Boolean> isAfk = new HashMap<>();
    public static HashMap<UUID, Location> lastLoc = new HashMap<>();
    public static HashMap<UUID, Long> lastAction = new HashMap<>();

    private final GahvilaCore plugin;

    public AFK(GahvilaCore plugin) {
        this.plugin = plugin;
    }

    public void setPlayerAFK(Player player, boolean afk) {
        UUID uuid = player.getUniqueId();
        PlaytimeCache playtimeCache = PlaytimeManager.playtimeCache.get(uuid);

        if (playtimeCache != null) {
            playtimeCache.setAfk(afk);
        }

        if (afk) {
            isAfk.put(uuid, true);
            player.sendMessage("Olet nyt afk.");
        } else {
            isAfk.remove(uuid);
            player.sendMessage("Et ole enää afk.");
        }
    }

    public void registerCommands() {
        new CommandAPICommand("afk")
                .executesPlayer((p, args) -> {
                    UUID uuid = p.getUniqueId();
                    if (!isAfk.containsKey(uuid)){
                        setPlayerAFK(p, true);
                    } else {
                        setPlayerAFK(p, false);
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
                if (lastLoc.get(uuid) == null || !lastLoc.get(uuid).equals(currentLocation)) {
                    long unixTime = System.currentTimeMillis();
                    long lastMovedTime = lastAction.containsKey(uuid) ? lastAction.get(uuid) : 0;
                    if (unixTime - lastMovedTime >= 150000){
                        setPlayerAFK(player, true);
                    }
                }
            }
        }, 0, 20).getTaskId();
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e){
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
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        lastLoc.put(uuid, p.getLocation());
        lastAction.put(uuid, System.currentTimeMillis());
        if (isAfk.containsKey(uuid)){
            setPlayerAFK(p, false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        setPlayerAFK(p, false);
        lastLoc.remove(uuid);
        lastAction.remove(uuid);
    }
}

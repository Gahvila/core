package net.gahvila.gahvilacore.Paper.Essentials;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.UUID;

public class AFK implements CommandExecutor, Listener {

    public static HashMap<UUID, Boolean> isAfk = new HashMap();

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

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if (!(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ())) {
            return;
        }
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
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
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerInteractEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(BlockBreakEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(BlockPlaceEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerJoinEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }

    @EventHandler
    public void onMove(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (isAfk.containsKey(uuid)){
            p.sendMessage("Et ole enää afk.");
            isAfk.remove(uuid);
        }
    }
}

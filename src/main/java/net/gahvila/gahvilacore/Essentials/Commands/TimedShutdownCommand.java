package net.gahvila.gahvilacore.Essentials.Commands;

import net.gahvila.gahvilacore.GahvilaCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TimedShutdownCommand implements CommandExecutor {
    public static HashMap<String, Boolean> rebooting = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender){
            if (!rebooting.containsKey("restart")){
                rebooting.put("restart", true);
                Bukkit.broadcastMessage("§cPalvelin käynnistyy uudelleen §e1 §cminuutin kuluttua!");
                Bukkit.getScheduler().runTaskLater(GahvilaCorePaper.instance, new Runnable() {
                    @Override
                    public void run() {
                        TimedShutdownCommand.reboot();
                    }
                }, 1000);
            }else{
                sender.sendMessage("Peruttu restart.");
                rebooting.clear();
            }
        }
        return false;
    }

    public static void reboot(){
        if (rebooting.containsKey("restart")){
            Bukkit.broadcastMessage("§c§lPalvelin käynnistyy uudelleen §e§l10 §c§lsekuntin kuluttua!");
            Bukkit.getScheduler().runTaskLater(GahvilaCorePaper.instance, new Runnable() {
                @Override
                public void run() {
                    if (rebooting.containsKey("restart")){
                        Bukkit.broadcastMessage("§c§lPalvelin käynnistyy uudelleen!");
                        for (Player players : Bukkit.getOnlinePlayers()){
                            players.kickPlayer("§c§lPalvelin käynnistyy uudelleen!");
                        }
                        Bukkit.shutdown();
                    }else{
                        Bukkit.broadcastMessage("§ePalvelimen uudelleenkäynnistys peruttiin.");
                    }
                }
            }, 200);
        }else{
            Bukkit.broadcastMessage("§ePalvelimen uudelleenkäynnistys peruttiin.");
        }
    }
}

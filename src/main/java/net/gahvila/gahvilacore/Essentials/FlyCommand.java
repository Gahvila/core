package net.gahvila.gahvilacore.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        String prefix = ChatColor.BLUE + "" + ChatColor.BOLD + "Gahvila" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY;

        if (args.length == 0) {
            if (!p.hasPermission("gahvilacore.fly.self")) return true;
            if (p.getAllowFlight()) {
                p.sendMessage(prefix + "Lentotilasi otettiin pois käytöstä.");
            } else {
                p.sendMessage(prefix + "Lentotilasi asetettiin käyttöön.");
            }
            p.setAllowFlight(!p.getAllowFlight());
            return true;
        }

        else if (args.length == 1) {
            if (!p.hasPermission("gahvilacore.fly.other")) return true;
            if (Bukkit.getPlayer(args[0]) == null) return true;
            Player p2 = Bukkit.getPlayer(args[0]);

            if (p2.getAllowFlight()) {
                p.sendMessage(prefix + "Pelaajan " + p2.getName() + " lentotila otettiin pois käytöstä.");
                p2.sendMessage(prefix + "Lentotilasi otettiin pois käytöstä.");
            } else {
                p.sendMessage(prefix + "Pelaajan " + p2.getName() + " lentotila asetettiin käyttöön.");
                p2.sendMessage(prefix + "Lentotilasi asetettiin käyttöön.");
            }
            p2.setAllowFlight(!p2.getAllowFlight());
            return true;

        }

        return false;
    }
}

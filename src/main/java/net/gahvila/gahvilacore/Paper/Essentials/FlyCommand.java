package net.gahvila.gahvilacore.Paper.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (args.length == 0) {
            if (!p.hasPermission("gahvilacore.fly.self")) return true;
            if (p.getAllowFlight()) {
                p.sendMessage("Lentotilasi otettiin pois käytöstä.");
            } else {
                p.sendMessage("Lentotilasi asetettiin käyttöön.");
            }
            p.setAllowFlight(!p.getAllowFlight());
            return true;
        }

        else if (args.length == 1) {
            if (!p.hasPermission("gahvilacore.fly.other")) return true;
            if (Bukkit.getPlayer(args[0]) == null) return true;
            Player p2 = Bukkit.getPlayer(args[0]);

            if (p2.getAllowFlight()) {
                p.sendMessage("Pelaajan " + p2.getName() + " lentotila otettiin pois käytöstä.");
                p2.sendMessage("Lentotilasi otettiin pois käytöstä.");
            } else {
                p.sendMessage("Pelaajan " + p2.getName() + " lentotila asetettiin käyttöön.");
                p2.sendMessage("Lentotilasi asetettiin käyttöön.");
            }
            p2.setAllowFlight(!p2.getAllowFlight());
            return true;

        }

        return false;
    }
}

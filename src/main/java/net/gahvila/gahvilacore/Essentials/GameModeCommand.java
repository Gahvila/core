package net.gahvila.gahvilacore.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        String prefix = ChatColor.BLUE + "" + ChatColor.BOLD + "Gahvila" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY;

        if (args.length == 1) {
            if (!p.hasPermission("gahvilacore.gamemode.self")) return true;
            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
                p.setGameMode(GameMode.SURVIVAL);
                p.sendMessage(prefix + "Pelitilasi on nyt selviytymistila.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative")) {
                p.setGameMode(GameMode.CREATIVE);
                p.sendMessage(prefix + "Pelitilasi on nyt luova.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
                p.setGameMode(GameMode.ADVENTURE);
                p.sendMessage(prefix + "Pelitilasi on nyt seikkailutila.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("spectator")) {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(prefix + "Pelitilasi on nyt katselutila.");
                return true;
            }
        }
        
        else if (args.length == 2) {

            if (!p.hasPermission("gahvilacore.gamemode.other")) return true;
            if (Bukkit.getPlayer(args[1]) == null) {
                p.sendMessage(prefix + ChatColor.RED + "Ei voitu löytää pelaajaa " + args[1] + "!");
                return true;
            }
            
            Player p2 = Bukkit.getPlayer(args[1]);

            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
                p2.setGameMode(GameMode.SURVIVAL);
                p2.sendMessage(prefix + "Pelitilasi on nyt selviytymistila.");
                p.sendMessage(prefix + "Asetit pelaajan " + p2.getName() + " pelitilaksi selviytymistila.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative")) {
                p2.setGameMode(GameMode.CREATIVE);
                p2.sendMessage(prefix + "Pelitilasi on nyt luova.");
                p.sendMessage(prefix + "Asetit pelaajan " + p2.getName() + " pelitilaksi luova.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
                p2.setGameMode(GameMode.ADVENTURE);
                p2.sendMessage(prefix + "Pelitilasi on nyt seikkailutila.");
                p.sendMessage(prefix + "Asetit pelaajan " + p2.getName() + " pelitilaksi seikkailutila.");
                return true;
            }

            else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("sp2") || args[0].equalsIgnoreCase("sp2ectator")) {
                p2.setGameMode(GameMode.SPECTATOR);
                p2.sendMessage(prefix + "Pelitilasi on nyt katselutila.");
                p.sendMessage(prefix + "Asetit pelaajan " + p2.getName() + " pelitilaksi katselutila.");
                return true;
            }
            
        }

        return false;
    }
}

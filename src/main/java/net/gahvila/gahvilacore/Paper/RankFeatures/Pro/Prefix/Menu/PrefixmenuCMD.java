package net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixmenuCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if (!sender.hasPermission("gahvilacore.rank.pro")) return true;
            ProPrefixMainMenu.openPrefixMainMenu((Player) sender);
            return true;
        }

        return false;
    }
}

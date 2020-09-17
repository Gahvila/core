package net.gahvila.gahvilacore.ChatFormat;

import net.gahvila.gahvilacore.ProGui.OpenGui;
import net.gahvila.gahvilacore.ProGui.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ChatFormatToggleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if (!sender.hasPermission("gahvilacore.chatformat.toggle")) return true;
            PrefixManager.enablePrefix((Player) sender, !PrefixManager.isPrefixEnabled((Player) sender));
            sender.sendMessage(ChatColor.GREEN + "Chatin muotoa muutettu onnistuneesti!");
            return true;
        }

        return false;
    }
}

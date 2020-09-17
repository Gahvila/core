package net.gahvila.gahvilacore.ProGui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if (!sender.hasPermission("gahvilacore.gui.pro")) return true;
            OpenGui.openMainGUI((Player) sender);
            return true;
        }

        return false;
    }
}

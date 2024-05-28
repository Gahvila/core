package net.gahvila.gahvilacore.Essentials.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ServerinfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = ((Player) sender).getPlayer();
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("§aInformaatio");
            p.sendMessage("§8| §fVersio: " + Bukkit.getVersionMessage());
            p.sendMessage("§8| §fLisäosat §7(§e" + Bukkit.getPluginManager().getPlugins().length +"§7)§f.: " + Arrays.toString(Bukkit.getPluginManager().getPlugins()));
            p.sendMessage("");
            p.sendMessage("§aSuorituskyky");
            p.sendMessage("§8| §fTPS: §e" + StrictMath.min(Bukkit.getServer().getTPS()[0], 20.0));
            p.sendMessage("§8| §fMSPT: §e" + Bukkit.getAverageTickTime());
            return true;
        }

        return false;
    }
}

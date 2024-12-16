package net.gahvila.gahvilacore.Panilla;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PanillaCommand {

    public void registerCommands() {
        new CommandAPICommand("panilla")
                .withPermission("panilla.command")
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("panilla.command.reload")
                        .executes((sender, args) -> {
                            sender.sendMessage("not implemented yet");
                            //TODO: implement reloading
                        }))
                .register();
    }

}

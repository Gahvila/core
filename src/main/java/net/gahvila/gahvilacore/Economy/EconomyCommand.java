package net.gahvila.gahvilacore.Economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import io.papermc.paper.ServerBuildInfo;
import net.gahvila.gahvilacore.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Marriage.MarriageMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class EconomyCommand {

    private final EconomyManager economyManager;


    public EconomyCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;

    }

    public void registerCommands() {
        new CommandAPICommand("temporarybalcommand")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("<white>Tililläsi on " + economyManager.getBalance(p.getUniqueId()) + " rahaa."));
                })
                .register();
        new CommandAPICommand("temporarybalcommandadmin")
                .withPermission("gahvilacore.adminbalance")
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(new OfflinePlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.addBalance(player.getUniqueId(), amount);
                            p.sendMessage("Lisätty pelaajalle <yellow>'" + player.getName() + "'</yellow> " +  amount +  " rahaa. Nyt: " + amount);
                        }))
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(new OfflinePlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.removeBalance(player.getUniqueId(), amount);
                            p.sendMessage("Vähennetty pelaajalta <yellow>'" + player.getName() + "'</yellow> " +  amount +  " rahaa. Nyt: " + amount);
                        }))
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new OfflinePlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.setBalance(player.getUniqueId(), amount);
                            p.sendMessage("Asetettu pelaajan " + player.getName() + " rahatilanne: " + amount);
                        }))
                .register();
    }
}

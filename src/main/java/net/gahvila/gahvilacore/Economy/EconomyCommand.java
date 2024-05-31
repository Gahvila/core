package net.gahvila.gahvilacore.Economy;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import org.bukkit.OfflinePlayer;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class EconomyCommand {

    private final EconomyManager economyManager;


    public EconomyCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;

    }

    public void registerCommands() {
        new CommandAPICommand("temporarybalcommand")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("<white>Tililläsi on " + economyManager.getBalance(p) + " rahaa."));
                })
                .register();
        new CommandAPICommand("temporarybalcommandadmin")
                .withPermission("gahvilacore.adminbalance")
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(new OfflinePlayerArgument("pelaaja"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.addBalance(player.getUniqueId(), amount);
                            p.sendMessage("Lisätty pelaajalle <yellow>'" + args.get("pelaaja") + "'</yellow> " +  amount +  " rahaa. Nyt: " + economyManager.getBalance(p));
                        }))
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(new OfflinePlayerArgument("pelaaja"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.removeBalance(player.getUniqueId(), amount);
                            p.sendMessage("Vähennetty pelaajalta <yellow>'" + args.get("pelaaja") + "'</yellow> " +  amount +  " rahaa. Nyt: " + economyManager.getBalance(p));
                        }))
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new OfflinePlayerArgument("pelaaja"))
                        .withArguments(new DoubleArgument("amount"))
                        .executesPlayer((p, args) -> {
                            OfflinePlayer player = (OfflinePlayer) args.get("pelaaja");
                            double amount = (double) args.get("amount");
                            economyManager.setBalance(player.getUniqueId(), amount);
                            p.sendMessage("Asetettu pelaajan " + args.get("pelaaja") + " rahatilanne: " + economyManager.getBalance(p));
                        }))
                .register();
    }
}

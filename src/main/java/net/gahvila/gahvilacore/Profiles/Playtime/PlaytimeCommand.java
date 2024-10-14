package net.gahvila.gahvilacore.Profiles.Playtime;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import io.papermc.paper.ServerBuildInfo;
import net.gahvila.gahvilacore.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlaytimeCommand {

    private final PlaytimeManager playtimeManager;

    public PlaytimeCommand(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    public void registerCommands() {
        new CommandAPICommand("playtime")
                .executesPlayer((p, args) -> {
                    long duration = playtimeManager.getPlaytime(p).join();
                    p.sendRichMessage("<yellow>" + ConfigManager.getServerName() + "</yellow> peliaika: <gray>" + PlaytimeManager.formatDuration(duration) + "</gray>.");
                })
                .register();
        new CommandAPICommand("playtimeadmin")
                .withPermission("gahvilacore.playtime.admin")
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(new OfflinePlayerArgument("pelaaja"))
                        .withArguments(new LongArgument("amount"))
                        .executes((sender, args) -> {
                            OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("pelaaja");
                            long amount = (long) args.get("amount");
                            playtimeManager.addPlaytime(offlinePlayer, amount)
                                    .thenRun(() -> sender.sendMessage("Nostit pelaajan " + offlinePlayer.getName() +" peliaikaa " + amount + " sekuntia!"))
                                    .exceptionally(ex -> {
                                        sender.sendMessage("Virhe pelaajan " + offlinePlayer.getName() + " peliaikaa nostaessa: " + ex.getMessage());
                                        return null;
                                    });
                        }))
                .withSubcommand(new CommandAPICommand("subtract")
                        .withArguments(new OfflinePlayerArgument("pelaaja"))
                        .withArguments(new LongArgument("amount"))
                        .executes((sender, args) -> {
                            OfflinePlayer offlinePlayer = (OfflinePlayer) args.get("pelaaja");
                            long amount = (long) args.get("amount");
                            playtimeManager.reducePlaytime(offlinePlayer, amount)
                                    .thenRun(() -> sender.sendMessage("Laskit pelaajan " + offlinePlayer.getName() +" peliaikaa " + amount + " sekuntia!"))
                                    .exceptionally(ex -> {
                                        sender.sendMessage("Virhe pelaajan " + offlinePlayer.getName() + " peliaikaa laskiessa: " + ex.getMessage());
                                        return null;
                                    });
                        }))
                .register();
    }
}

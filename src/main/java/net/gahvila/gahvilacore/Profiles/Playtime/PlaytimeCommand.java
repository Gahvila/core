package net.gahvila.gahvilacore.Profiles.Playtime;

import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PlaytimeCommand {

    private final PlaytimeManager playtimeManager;

    public PlaytimeCommand(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    public void registerCommands() {
        new CommandAPICommand("playtime")
                .withPermission("gahvilacore.playtime")
                .executesPlayer((p, args) -> {
                    p.sendRichMessage(String.valueOf(playtimeManager.getPlaytimeDuration(p).join()));
                })
                .register();
    }
}

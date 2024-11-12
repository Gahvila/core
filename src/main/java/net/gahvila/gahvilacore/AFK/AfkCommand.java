package net.gahvila.gahvilacore.AFK;

import dev.jorel.commandapi.CommandAPICommand;

public class AfkCommand {

    public void registerCommands() {
        new CommandAPICommand("afk")
                .executesPlayer((player, args) -> {
                    AfkManager.setPlayerAFK(player, AfkManager.isPlayerAfk(player));
                })
                .register();
    }
}
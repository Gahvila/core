package net.gahvila.gahvilacore.AFK;

import dev.jorel.commandapi.CommandAPICommand;

public class AfkCommand {

    private final AfkManager afkManager;

    public AfkCommand(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    public void registerCommands() {
        new CommandAPICommand("afk")
                .executesPlayer((player, args) -> {
                    afkManager.setPlayerAFK(player, afkManager.isPlayerAfk(player));
                })
                .register();
    }
}
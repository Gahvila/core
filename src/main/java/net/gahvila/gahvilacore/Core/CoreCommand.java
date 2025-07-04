package net.gahvila.gahvilacore.Core;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;

public class CoreCommand {

    private final TeleportManager teleportManager;

    public CoreCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    public void registerCommands() {
        new CommandAPICommand("gahvilacore")
                .withPermission("gahvilacore.command.admin")
                .withAliases("gc")
                .executes((sender, args) -> {
                    sender.sendRichMessage("<red>Kelvottomat argumentit.");
                })
                .withSubcommand(new CommandAPICommand("debug")
                        .executes((sender, args) -> {
                            boolean isDebugging = DebugMode.isDebugging();
                            DebugMode.setDebugging(!isDebugging);
                            sender.sendRichMessage(isDebugging
                                    ? "<red>Debug kytketty pois päältä." //turning debug off
                                    : "<green>Debug kytketty päälle."); //turning debug on
                        }))
                .withSubcommand(new CommandAPICommand("setspawn")
                        .executesPlayer((p, args) -> {
                            teleportManager.saveTeleport(GahvilaCore.instance,"spawn", p.getLocation());
                            p.sendMessage("Asetit spawnin uuden sijainnin.");
                        }))
                .register();
    }
}

package net.gahvila.gahvilacore.Core;

import dev.jorel.commandapi.CommandAPICommand;

public class CoreCommand {

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
                .register();
    }
}

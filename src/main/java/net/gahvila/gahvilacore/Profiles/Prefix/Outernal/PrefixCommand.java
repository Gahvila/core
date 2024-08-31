package net.gahvila.gahvilacore.Profiles.Prefix.Outernal;

import dev.jorel.commandapi.CommandAPICommand;

public class PrefixCommand {

    private final PrefixColorMenu prefixGradientMenu;

    public PrefixCommand(PrefixColorMenu prefixGradientMenu) {
        this.prefixGradientMenu = prefixGradientMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("prefixmenu")
                .withAliases("prefix")
                .withPermission("gahvilacore.prefixmenu")
                .executesPlayer((p, args) -> {
                    prefixGradientMenu.showGUI(p);
                })
                .register();
    }
}

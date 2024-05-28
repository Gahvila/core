package net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu;

import dev.jorel.commandapi.CommandAPICommand;

public class PrefixmenuCMD {

    public void registerCommands() {
        new CommandAPICommand("prefixmenu")
                .withAliases("pro")
                .withPermission("gahvilacore.rank.pro")
                .executesPlayer((p, args) -> {
                    ProPrefixMainMenu.openPrefixMainMenu(p);
                })
                .register();
    }
}

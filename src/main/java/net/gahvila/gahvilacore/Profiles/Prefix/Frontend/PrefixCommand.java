package net.gahvila.gahvilacore.Profiles.Prefix.Frontend;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixColorMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixMainMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixTypeMenu;

import java.util.UUID;

public class PrefixCommand {

    private final PrefixTypeMenu prefixTypeMenu;
    private final PrefixMainMenu prefixMainMenu;
    private final PrefixColorMenu prefixColorMenu;

    public PrefixCommand(PrefixTypeMenu prefixTypeMenu, PrefixMainMenu prefixMainMenu, PrefixColorMenu prefixColorMenu) {
        this.prefixTypeMenu = prefixTypeMenu;
        this.prefixMainMenu = prefixMainMenu;
        this.prefixColorMenu = prefixColorMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("prefix")
                .withPermission("gahvilacore.prefixmenu")
                .withSubcommand(new CommandAPICommand("colorpicker")
                        .executesPlayer((player, args) -> {
                            prefixColorMenu.showGUI(player);
                        }))
                .withSubcommand(new CommandAPICommand("setprefix")
                        .executesPlayer((player, args) -> {
                            prefixTypeMenu.showGUI(player);
                        }))
                .executesPlayer((p, args) -> {
                    prefixMainMenu.showGUI(p);
                })
                .register();

    }
}

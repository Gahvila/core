package net.gahvila.gahvilacore.Profiles.Prefix.Frontend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Gradient;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixTypes;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixColorMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixMainMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixTypeMenu;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PrefixCommand {

    private final PrefixTypeMenu prefixTypeMenu;
    private final PrefixMainMenu prefixMainMenu;
    private final PrefixColorMenu prefixColorMenu;
    private final PrefixManager prefixManager;

    public PrefixCommand(PrefixTypeMenu prefixTypeMenu, PrefixMainMenu prefixMainMenu, PrefixColorMenu prefixColorMenu, PrefixManager prefixManager) {
        this.prefixTypeMenu = prefixTypeMenu;
        this.prefixMainMenu = prefixMainMenu;
        this.prefixColorMenu = prefixColorMenu;
        this.prefixManager = prefixManager;
    }

    public void registerCommands() {
        new CommandAPICommand("prefix")
                .withPermission("gahvilacore.prefixmenu")
                .withSubcommand(new CommandAPICommand("setcolor")
                        .withSubcommand(new CommandAPICommand("single")
                                .withArguments(customSingleArgument("color"))
                                .executesPlayer((player, args) -> {
                                    Single single = Single.valueOf(args.getRaw("color"));
                                    if (player.hasPermission(single.getPermissionNode())) {
                                        prefixManager.setPrefixType(player, PrefixTypes.SINGLE);
                                        prefixManager.setSingle(player, single);
                                        player.sendRichMessage("<white>single: " + single.name() + "</white>");
                                    } else {
                                        player.sendMessage("Ei oikeuksia.");
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand("gradient")
                                .withArguments(customGradientArgument("color"))
                                .executesPlayer((player, args) -> {
                                    Gradient gradient = Gradient.valueOf(args.getRaw("color"));
                                    if (player.hasPermission(gradient.getPermissionNode())) {
                                        prefixManager.setPrefixType(player, PrefixTypes.GRADIENT);
                                        prefixManager.setGradient(player, gradient);
                                        player.sendRichMessage("<white>gradient: " + gradient.name() + "</white>");
                                    } else {
                                        player.sendMessage("Ei oikeuksia.");
                                    }
                                }))
                        .executesPlayer((player, args) -> {
                            prefixColorMenu.showGUI(player);
                        }))
                .withSubcommand(new CommandAPICommand("setprefix")
                        .withOptionalArguments(customPrefixArgument("prefix"))
                        .executesPlayer((player, args) -> {
                            if (args.getRaw("prefix") == null) {
                                prefixTypeMenu.showGUI(player);
                                return;
                            }

                            Prefix prefix = Prefix.valueOf(args.getRaw("prefix"));
                            if (player.hasPermission(prefix.getPermissionNode())) {
                                prefixManager.setPrefix(player, prefix);
                                player.sendRichMessage("<white>prefix: " + prefix.name() + "</white>");
                            } else {
                                player.sendMessage("Ei oikeuksia.");
                            }
                        }))
                .executesPlayer((p, args) -> {
                    prefixMainMenu.showGUI(p);
                })
                .register();

    }

    public Argument<Prefix> customPrefixArgument(String nodeName) {
        return new CustomArgument<Prefix, String>(new StringArgument(nodeName), info -> {
            return Prefix.valueOf(info.input());
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            return Arrays.stream(Prefix.values())
                    .filter(prefix -> player.hasPermission(prefix.getPermissionNode()))
                    .map(Prefix::name)
                    .toArray(String[]::new);
        }));
    }

    public Argument<Single> customSingleArgument(String nodeName) {
        return new CustomArgument<Single, String>(new StringArgument(nodeName), info -> {
            return Single.valueOf(info.input());
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            return Arrays.stream(Single.values())
                    .filter(single -> player.hasPermission(single.getPermissionNode()))
                    .map(Single::name)
                    .toArray(String[]::new);
        }));
    }

    public Argument<Gradient> customGradientArgument(String nodeName) {
        return new CustomArgument<Gradient, String>(new StringArgument(nodeName), info -> {
            return Gradient.valueOf(info.input());
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            return Arrays.stream(Gradient.values())
                    .filter(gradient -> player.hasPermission(gradient.getPermissionNode()))
                    .map(Gradient::name)
                    .toArray(String[]::new);
        }));
    }
}

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
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixColorDialog;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixMainDialog;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixTypeDialog;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PrefixCommand {

    private final PrefixTypeDialog prefixTypeDialog;
    private final PrefixMainDialog prefixMainDialog;
    private final PrefixColorDialog prefixColorDialog;
    private final PrefixManager prefixManager;

    public PrefixCommand(PrefixTypeDialog prefixTypeDialog, PrefixMainDialog prefixMainDialog, PrefixColorDialog prefixColorDialog, PrefixManager prefixManager) {
        this.prefixTypeDialog = prefixTypeDialog;
        this.prefixMainDialog = prefixMainDialog;
        this.prefixColorDialog = prefixColorDialog;
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
                                        player.sendRichMessage("<white>Asetit itsellesi väriksi</white> <" + single.getColor() + ">" + single.getDisplayName() + "<white>.</white>");
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
                                        player.sendRichMessage("<white>Asetit itsellesi liukuväriksi</white> <gradient:" + gradient.getGradient() + ">" + gradient.getDisplayName() + "<white>.</white>");
                                    } else {
                                        player.sendMessage("Ei oikeuksia.");
                                    }
                                }))
                        .executesPlayer((player, args) -> {
                            prefixColorDialog.show(player);
                        }))
                .withSubcommand(new CommandAPICommand("setprefix")
                        .withOptionalArguments(customPrefixArgument("prefix"))
                        .executesPlayer((player, args) -> {
                            if (args.getRaw("prefix") == null) {
                                prefixTypeDialog.show(player);
                                return;
                            }

                            Prefix prefix = Prefix.valueOf(args.getRaw("prefix"));
                            if (player.hasPermission(prefix.getPermissionNode())) {
                                prefixManager.setPrefix(player, prefix);
                                player.sendRichMessage("<white>Asetit itsellesi etuliitteeksi " + prefixManager.generatePrefix(player) + ".</white>");
                            } else {
                                player.sendMessage("Ei oikeuksia.");
                            }
                        }))
                .executesPlayer((p, args) -> {
                    prefixMainDialog.show(p);
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

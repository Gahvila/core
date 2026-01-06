package net.gahvila.gahvilacore.Profiles.Prefix.Frontend;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
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

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("prefix")
                .executes(this::execute)
                .then(Commands.literal("setcolor")
                        .then(Commands.literal("single")
                            .then(Commands.argument("color", StringArgumentType.string())
                                    .suggests((ctx, builder) -> {
                                        Arrays.stream(Single.values())
                                                .filter(single -> ctx.getSource().getSender().hasPermission(single.getPermissionNode()))
                                                .map(Single::name)
                                                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(this::executeSetSingle)
                            )
                        )
                        .then(Commands.literal("gradient")
                                .then(Commands.argument("color", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            Arrays.stream(Gradient.values())
                                                    .filter(gradient -> ctx.getSource().getSender().hasPermission(gradient.getPermissionNode()))
                                                    .map(Gradient::name)
                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(this::executeSetGradient)
                                )
                        )
                        .executes(this::executeColor)
                )
                .then(Commands.literal("setprefix")
                        .then(Commands.argument("prefix", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    Arrays.stream(Prefix.values())
                                            .filter(prefix -> ctx.getSource().getSender().hasPermission(prefix.getPermissionNode()))
                                            .map(Prefix::name)
                                            .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(this::executeSetPrefix)
                        )
                        .executes(this::executePrefixType)
                )
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            prefixMainDialog.show(player);
        }
        return 1;
    }

    private int executeColor(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            prefixColorDialog.show(player);
        }
        return 1;
    }

    private int executePrefixType(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            prefixTypeDialog.show(player);
        }
        return 1;
    }

    private int executeSetSingle(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            String inputArg = context.getArgument("color", String.class);
            Single single;

            try {
                single = Single.valueOf(inputArg);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Tuota väriä ei ole.");
                return 0;
            }

            if (player.hasPermission(single.getPermissionNode())) {
                prefixManager.setPrefixType(player, PrefixTypes.SINGLE);
                prefixManager.setSingle(player, single);
                player.sendRichMessage("<white>Asetit itsellesi väriksi</white> <" + single.getColor() + ">" + single.getDisplayName() + "<white>.</white>");
            } else {
                player.sendMessage("Ei oikeuksia.");
            }
        }
        return 1;
    }

    private int executeSetGradient(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            String inputArg = context.getArgument("color", String.class);
            Gradient gradient;

            try {
                gradient = Gradient.valueOf(inputArg);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Tuota väriä ei ole.");
                return 0;
            }

            if (player.hasPermission(gradient.getPermissionNode())) {
                prefixManager.setPrefixType(player, PrefixTypes.GRADIENT);
                prefixManager.setGradient(player, gradient);
                player.sendRichMessage("<white>Asetit itsellesi liukuväriksi</white> <gradient:" + gradient.getGradient() + ">" + gradient.getDisplayName() + "<white>.</white>");
            } else {
                player.sendMessage("Ei oikeuksia.");
            }
        }
        return 1;
    }

    private int executeSetPrefix(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            String inputArg = context.getArgument("prefix", String.class);
            Prefix prefix;

            try {
                prefix = Prefix.valueOf(inputArg);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Tuota etuliitettä ei ole.");
                return 0;
            }

            if (player.hasPermission(prefix.getPermissionNode())) {
                prefixManager.setPrefix(player, prefix);
                player.sendRichMessage("<white>Asetit itsellesi etuliitteeksi " + prefixManager.generatePrefix(player) + ".</white>");
            } else {
                player.sendMessage("Ei oikeuksia.");
            }
        }
        return 1;
    }
}

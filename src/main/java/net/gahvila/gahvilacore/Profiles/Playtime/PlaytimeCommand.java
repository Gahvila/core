package net.gahvila.gahvilacore.Profiles.Playtime;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    private final PlaytimeManager playtimeManager;

    public PlaytimeCommand(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(createPlaytime());
            commands.registrar().register(createPlaytimeAdmin());
        });
    }

    private LiteralCommandNode<CommandSourceStack> createPlaytime() {
        return Commands.literal("playtime").executes(this::executePlaytime).build();
    }

    private LiteralCommandNode<CommandSourceStack> createPlaytimeAdmin() {
        return Commands.literal("playtimeadmin")
                .requires(source -> source.getSender().hasPermission("gahvilacore.command.admin"))

                .then(Commands.literal("add")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(this::executePlaytimeAdminAdd)
                                )
                        )
                )
                .then(Commands.literal("subtract")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(this::executePlaytimeAdminSubtract)
                                )
                        )
                )
                .build();
    }

    private int executePlaytime(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            long duration = playtimeManager.getPlaytime(player).join();
            player.sendRichMessage("<yellow>" + ConfigManager.getServerName() + "</yellow> peliaika: <gray>" + PlaytimeManager.formatDuration(duration) + "</gray>.");
        }
        return 1;
    }

    private int executePlaytimeAdminAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();

        final long amount = context.getArgument("amount", Long.class);
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        playtimeManager.addPlaytime(target, amount)
                .thenRun(() -> sender.sendMessage("Nostit pelaajan " + target.getName() +" peliaikaa " + amount + " sekuntia!"))
                .exceptionally(ex -> {
                    sender.sendMessage("Virhe pelaajan " + target.getName() + " peliaikaa nostaessa: " + ex.getMessage());
                    return null;
                });
        return 1;
    }

    private int executePlaytimeAdminSubtract(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();

        final long amount = context.getArgument("amount", Long.class);
        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        playtimeManager.reducePlaytime(target, amount)
                .thenRun(() -> sender.sendMessage("Laskit pelaajan " + target.getName() +" peliaikaa " + amount + " sekuntia!"))
                .exceptionally(ex -> {
                    sender.sendMessage("Virhe pelaajan " + target.getName() + " peliaikaa laskiessa: " + ex.getMessage());
                    return null;
                });
        return 1;
    }
}

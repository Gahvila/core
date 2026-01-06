package net.gahvila.gahvilacore.Essentials.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    private static final String PERM_BASE = "gahvilacore.command.gamemode";
    private static final String PERM_OTHERS = "gahvilacore.command.gamemode.others";

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
            commands.registrar().register(createAlias());

            commands.registrar().register(createShortcut("gmc", GameMode.CREATIVE));
            commands.registrar().register(createShortcut("gms", GameMode.SURVIVAL));
            commands.registrar().register(createShortcut("gma", GameMode.ADVENTURE));
            commands.registrar().register(createShortcut("gmsp", GameMode.SPECTATOR));
        });
    }


    private LiteralCommandNode<CommandSourceStack> create() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("gamemode")
                .requires(source -> source.getSender().hasPermission(PERM_BASE));

        root.then(Commands.argument("gamemode", ArgumentTypes.gameMode())
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(source -> source.getSender().hasPermission(PERM_OTHERS))
                        .executes(this::executeSetGamemodeOthers)
                )
                .executes(this::executeSetGamemode)
        );
        return root.build();
    }

    private LiteralCommandNode<CommandSourceStack> createAlias() {
        return Commands.literal("gm")
                .requires(source -> source.getSender().hasPermission(PERM_BASE)) // Added permission check here
                .redirect(create())
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createShortcut(String commandName, GameMode mode) {
        return Commands.literal(commandName)
                .requires(source -> source.getSender().hasPermission(PERM_BASE))
                .executes(ctx -> executeShortcut(ctx, mode))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(source -> source.getSender().hasPermission(PERM_OTHERS))
                        .executes(ctx -> executeShortcutOthers(ctx, mode))
                )
                .build();
    }

    private int executeSetGamemode(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        GameMode gamemode = context.getArgument("gamemode", GameMode.class);
        return setGamemodeLogic(sender, sender, gamemode);
    }

    private int executeSetGamemodeOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        GameMode gamemode = context.getArgument("gamemode", GameMode.class);
        Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();

        return setGamemodeLogic(sender, target, gamemode);
    }

    private int executeShortcut(CommandContext<CommandSourceStack> context, GameMode mode) {
        CommandSender sender = context.getSource().getSender();
        return setGamemodeLogic(sender, sender, mode);
    }

    private int executeShortcutOthers(CommandContext<CommandSourceStack> context, GameMode mode) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();

        return setGamemodeLogic(sender, target, mode);
    }

    private int setGamemodeLogic(CommandSender sender, CommandSender targetSender, GameMode gamemode) {
        if (targetSender instanceof Player target) {
            target.setGameMode(gamemode);

            Component modeName = Component.translatable(gamemode);

            if (sender.equals(target)) {
                sender.sendRichMessage("Asetit pelitilan <yellow><gamemode></yellow> itsellesi.",
                        Placeholder.component("gamemode", modeName));
            } else {
                sender.sendRichMessage("Asetit pelitilan <yellow><gamemode></yellow> pelaajalle <yellow>" + target.getName() + "</yellow>.",
                        Placeholder.component("gamemode", modeName));
                target.sendRichMessage("Sinulle asetettiin pelitila <yellow><gamemode></yellow>.",
                        Placeholder.component("gamemode", modeName));
            }
        } else {
            sender.sendRichMessage("Kohde ei ole pelaaja tai sinun täytyy syöttää pelaajan nimi.");
        }
        return Command.SINGLE_SUCCESS;
    }
}
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

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
            commands.registrar().register(createAlias());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("gamemode")
                .requires(source -> source.getSender().hasPermission("gahvilacore.command.gamemode"));

        root.then(Commands.argument("gamemode", ArgumentTypes.gameMode())
            .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(source -> source.getSender().hasPermission("gahvilacore.command.gamemode.others"))
                    .executes(this::executeSetGamemodeOthers)
            )
            .executes(this::executeSetGamemode)
        );
        return root.build();
    }

    private LiteralCommandNode<CommandSourceStack> createAlias() {
        return Commands.literal("gm")
                .redirect(create())
                .build();
    }

    private int executeSetGamemode(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        final GameMode gamemode = context.getArgument("gamemode", GameMode.class);

        if (sender instanceof Player target) {
            target.setGameMode(gamemode);
            target.sendRichMessage("Asetit pelitilan <yellow><gamemode></yellow> itsellesi.", Placeholder.component("gamemode", Component.translatable(gamemode)));
        } else {
            sender.sendRichMessage("Sinun täytyy syöttää pelaajan nimi");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeSetGamemodeOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        final GameMode gamemode = context.getArgument("gamemode", GameMode.class);

        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        target.setGameMode(gamemode);
        sender.sendRichMessage("Asetit pelitilan <yellow><gamemode></yellow> pelaajalle <yellow>" + target.getName() + "</yellow>.", Placeholder.component("gamemode", Component.translatable(gamemode)));
        target.sendRichMessage("Sinulle asetettiin pelitila <yellow><gamemode></yellow>.", Placeholder.component("gamemode", Component.translatable(gamemode)));
        return Command.SINGLE_SUCCESS;
    }
}

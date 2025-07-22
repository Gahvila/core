package net.gahvila.gahvilacore.Essentials.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class SpeedCommand {

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("speed")
                .requires(source -> source.getSender().hasPermission("gahvilacore.command.speed"));

        root.then(Commands.argument("speed", IntegerArgumentType.integer(1, 5))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(source -> source.getSender().hasPermission("gahvilacore.command.speed.others"))
                        .executes(this::executeSetSpeedOthers)
                )
                .executes(this::executeSetSpeed)
        );
        return root.build();
    }

    private int executeSetSpeed(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        final int speed = context.getArgument("speed", Integer.class);

        if (sender instanceof Player target) {
            if (speed >= 1 && speed <= 5) {
                target.setWalkSpeed(0.1F * speed + 0.1F);
                target.setFlySpeed(0.1F * speed);

                target.sendRichMessage("Asetit nopeuden <yellow>" + speed + "</yellow> itsellesi.");
            } else {
                sender.sendMessage("Kelvoton nopeus.");
            }
        } else {
            sender.sendRichMessage("Sinun täytyy syöttää pelaajan nimi");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeSetSpeedOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        final int speed = context.getArgument("speed", Integer.class);

        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(context.getSource()).getFirst();

        if (speed >= 1 && speed <= 5) {
            target.setWalkSpeed(0.1F * speed + 0.1F);
            target.setFlySpeed(0.1F * speed);

            sender.sendRichMessage(("Asetit nopeuden <yellow>" + speed + "</yellow> pelaajalle <yellow>" + target.getName() + "</yellow>."));
            target.sendRichMessage("Sinulle asetettiin nopeus <yellow>" + speed + "</yellow>.");
        } else {
            sender.sendMessage("Kelvoton nopeus.");
        }
        return Command.SINGLE_SUCCESS;
    }
}

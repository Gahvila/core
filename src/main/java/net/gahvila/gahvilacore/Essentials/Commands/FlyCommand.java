package net.gahvila.gahvilacore.Essentials.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class FlyCommand {

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("fly")
                .requires(source -> source.getSender().hasPermission("gahvilacore.command.fly"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            if (player.getAllowFlight()){
                player.sendRichMessage("Lentotila: <red>pois päältä");
                player.setAllowFlight(false);
                player.setFlying(false);
            } else {
                player.sendRichMessage("Lentotila: <green>päällä");
                player.setAllowFlight(true);

                Vector velocity = player.getVelocity();
                velocity.setY(0.5);
                player.setVelocity(velocity);

                Bukkit.getScheduler().runTaskLater(instance, () -> player.setFlying(true), 0);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

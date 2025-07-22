package net.gahvila.gahvilacore.AFK;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand {

    private final AfkManager afkManager;

    public AfkCommand(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("afk")
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            boolean currentlyAfk = afkManager.isPlayerAfk(player);
            afkManager.setPlayerAFK(player, !currentlyAfk);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}
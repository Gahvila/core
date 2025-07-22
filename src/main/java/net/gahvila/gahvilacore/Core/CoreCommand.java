package net.gahvila.gahvilacore.Core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.AFK.AfkCommand;
import net.gahvila.gahvilacore.AFK.AfkManager;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand {

    private final TeleportManager teleportManager;

    public CoreCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
            commands.registrar().register(createAlias());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("gahvilacore")
                .requires(source -> source.getSender().hasPermission("gahvilacore.command.admin"))
                .executes(this::execute)
                .then(Commands.literal("debug").executes(this::executeDebug))
                .then(Commands.literal("setspawn").executes(this::executeSetSpawn))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createAlias() {
        return Commands.literal("gc")
                .redirect(create())
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendRichMessage("<red>Kelvottomat argumentit.");
        return 1;
    }

    private int executeDebug(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        boolean isDebugging = DebugMode.isDebugging();
        DebugMode.setDebugging(!isDebugging);
        sender.sendRichMessage(isDebugging
                ? "<red>Debug kytketty pois päältä."
                : "<green>Debug kytketty päälle.");
        return 1;
    }

    private int executeSetSpawn(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player player)) return 0;
        teleportManager.saveTeleport(GahvilaCore.instance, "spawn", player.getLocation());
        player.sendMessage("Asetit spawnin uuden sijainnin.");
        return 1;
    }
}

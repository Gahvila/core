package net.gahvila.gahvilacore.Teleport.Spawn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.lang.Float.MAX_VALUE;

public class SpawnCommand {
    private final TeleportManager teleportManager;

    public SpawnCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("spawn")
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            Location loc = teleportManager.getTeleport(GahvilaCore.instance, "spawn");
            if (loc == null) {
                player.sendMessage("Spawn-sijaintia ei ole asetettu.");
                return 0;
            }
            player.teleportAsync(loc);
            player.sendMessage("Teleporttasit spawnille.");
            player.playSound(loc, Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}

package net.gahvila.gahvilacore.Essentials.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class InfoCommands {

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(createDiscordCommand());
            commands.registrar().register(createServerInfoCommand());
            commands.registrar().register(createRulesCommand());
        });
    }

    private static LiteralCommandNode<CommandSourceStack> createDiscordCommand() {
        return Commands.literal("discord")
                .executes(context -> {
                    context.getSource().getSender().sendRichMessage("<white>Gahvilan Discordin osoite: <yellow><click:open_url:'https://dc.gahvila.net'>dc.gahvila.net</click></yellow>");
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    private static LiteralCommandNode<CommandSourceStack> createServerInfoCommand() {
        return Commands.literal("serverinfo")
                .executes(context -> {
                    final ServerBuildInfo build = ServerBuildInfo.buildInfo();
                    List<String> plugins = new ArrayList<>();
                    for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                        plugins.add(plugin.getName());
                    }
                    int buildNumber = build.buildNumber().isPresent() ? build.buildNumber().getAsInt() : 0;
                    context.getSource().getSender().sendRichMessage("Tämä palvelin käyttää <yellow>" + build.brandName()
                            + " " + build.minecraftVersionName() + "</yellow> <white>koontiversiota</white><yellow> " + buildNumber + "</yellow>.<br><br>" +
                            "Palvelimella on <yellow>" + plugins.size() + "</yellow> lisäosaa: <yellow>" + String.join("<white>,<yellow> ", plugins));
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    private static LiteralCommandNode<CommandSourceStack> createRulesCommand() {
        return Commands.literal("rules")
                .executes(context -> {
                    context.getSource().getSender().sendRichMessage("<white>Säännöt löytyvät osoitteesta: <yellow><click:open_url:'https://gahvila.net/rules'>gahvila.net/rules</click></yellow>");
                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}

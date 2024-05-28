package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class InfoCommands {

    public void registerCommands() {
        new CommandAPICommand("discord")
                .withAliases("dc")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("<white>Gahvilan Discordin osoite: <yellow><click:open_url:'https://dc.gahvila.net'>dc.gahvila.net</click></yellow>"));
                })
                .register();
        new CommandAPICommand("serverinfo")
                .executesPlayer((p, args) -> {
                    final ServerBuildInfo build = ServerBuildInfo.buildInfo();
                    List<String> plugins = new ArrayList<>();
                    for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                        plugins.add(plugin.getName());
                    }

                    p.sendMessage(toMM("Tämä palvelin käyttää Minecraft-versiota <yellow>" + build.minecraftVersionName()
                            + " " + build.brandName() + "</yellow> <white>koontiversiota</white><yellow> " + build.buildNumber() + "</yellow><br><br>" +
                            "Palvelimella on <yellow>" + plugins.size() + "</yellow> lisäosaa: <yellow>" + String.join("<white>,<yellow> ", plugins)));
                })
                .register();
        new CommandAPICommand("perf")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("TPS: <yellow>" + Arrays.toString(Bukkit.getTPS()) + "</yellow><br>" +
                            "MSPT: <gold>" + Bukkit.getAverageTickTime() + "</gold>"));
                })
                .register();
    }
}
